using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using SpotiXeApi.DTOs;
using SpotiXeApi.Entities;
using SpotiXeApi.Services;
using SpotiXeApi.Utils;

namespace SpotiXeApi.Controllers;

/// <summary>
/// Controller xử lý authentication
/// </summary>
[ApiController]
[Route("auth")]
public class AuthController : ControllerBase
{
    private readonly FirebaseService _firebaseService;
    private readonly JwtService _jwtService;
    private readonly UserService _userService;
    private readonly ILogger<AuthController> _logger;
    private readonly EmailOtpService _emailOtpService;
    private readonly EmailSenderService _emailService;

    public AuthController(
        FirebaseService firebaseService,
        JwtService jwtService,
        UserService userService,
        ILogger<AuthController> logger,
        EmailOtpService emailOtpService,
        EmailSenderService emailService
    )
    {
        _firebaseService = firebaseService;
        _jwtService = jwtService;
        _userService = userService;
        _logger = logger;
        _emailOtpService = emailOtpService;
        _emailService = emailService;
    }

    /// <summary>
    /// Đăng nhập bằng Firebase ID Token (hỗ trợ Google, Email OTP, Phone OTP)
    /// </summary>
    /// <remarks>
    /// Client gửi Firebase ID Token trong Authorization header:
    /// Authorization: Bearer {firebase_id_token}
    /// 
    /// Backend sẽ:
    /// 1. Verify Firebase token
    /// 2. Tìm hoặc tạo user trong database
    /// 3. Tạo JWT token riêng của hệ thống
    /// 4. Trả về JWT token + thông tin user
    /// </remarks>
    /// <response code="200">Đăng nhập thành công</response>
    /// <response code="401">Token không hợp lệ hoặc hết hạn</response>
    /// <response code="500">Lỗi server</response>
    [HttpPost("login")]
    [AllowAnonymous]
    [ProducesResponseType(typeof(LoginResponseDto), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(ErrorResponseDto), StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(typeof(ErrorResponseDto), StatusCodes.Status500InternalServerError)]
    public async Task<IActionResult> Login()
    {
        try
        {
            // 1. Lấy Firebase ID Token từ Authorization header
            var authHeader = Request.Headers["Authorization"].ToString();
            if (string.IsNullOrEmpty(authHeader) || !authHeader.StartsWith("Bearer "))
            {
                _logger.LogWarning("Missing or invalid Authorization header");
                return Unauthorized(new ErrorResponseDto
                {
                    Message = "Missing or invalid Authorization header. Expected format: 'Bearer {firebase_id_token}'"
                });
            }

            var firebaseToken = authHeader.Substring("Bearer ".Length).Trim();

            // 2. Verify Firebase token và lấy thông tin user
            _logger.LogInformation("Verifying Firebase token...");
            FirebaseUserInfo firebaseUserInfo;
            try
            {
                firebaseUserInfo = await _firebaseService.VerifyIdTokenAsync(firebaseToken);
            }
            catch (UnauthorizedAccessException ex)
            {
                _logger.LogWarning($"Firebase token verification failed: {ex.Message}");
                return Unauthorized(new ErrorResponseDto
                {
                    Message = "Invalid or expired Firebase token",
                    Details = ex.Message
                });
            }

            // Kiểm tra tài khoản Firebase có bị disabled không
            if (firebaseUserInfo.Disabled)
            {
                _logger.LogWarning($"Disabled Firebase account attempted login: {firebaseUserInfo.Uid}");
                return Unauthorized(new ErrorResponseDto
                {
                    Message = "Your account has been disabled"
                });
            }

            _logger.LogInformation($"Firebase token verified successfully. UID: {firebaseUserInfo.Uid}");

            // 3. Tìm hoặc tạo user trong database
            var user = await _userService.FindOrCreateUserAsync(firebaseUserInfo);

            // Kiểm tra user có active không
            if (user.IsActive == 0UL)
            {
                _logger.LogWarning($"Inactive user attempted login: {user.UserId}");
                return Unauthorized(new ErrorResponseDto
                {
                    Message = "Your account is inactive. Please contact support."
                });
            }

            _logger.LogInformation($"User authenticated: {user.UserId}");

            // 4. Tạo JWT token của hệ thống
            var jwtToken = _jwtService.GenerateToken(
                userId: user.UserId,
                email: user.Email,
                username: user.Username,
                role: "User" // Mặc định role = User, có thể mở rộng sau
            );

            // 5. Tạo response
            var response = new LoginResponseDto
            {
                Success = true,
                Token = jwtToken,
                ExpiresIn = _jwtService.GetExpireSeconds(),
                User = _userService.MapToDto(user)
            };

            _logger.LogInformation($"Login successful for user {user.UserId}");

            return Ok(response);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Unexpected error during login");
            return StatusCode(500, new ErrorResponseDto
            {
                Message = "An error occurred during login",
                Details = ex.Message
            });
        }
    }

    /// <summary>
    /// Test endpoint để kiểm tra JWT token có hoạt động không
    /// </summary>
    /// <remarks>
    /// Endpoint này yêu cầu JWT token hợp lệ trong Authorization header.
    /// Dùng để test sau khi login thành công.
    /// </remarks>
    /// <response code="200">Token hợp lệ</response>
    /// <response code="401">Token không hợp lệ hoặc hết hạn</response>
    [HttpGet("me")]
    [Authorize]
    [ProducesResponseType(typeof(object), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public IActionResult GetCurrentUser()
    {
        var userId = User.FindFirst(System.Security.Claims.ClaimTypes.NameIdentifier)?.Value;
        var email = User.FindFirst(System.Security.Claims.ClaimTypes.Email)?.Value;
        var username = User.FindFirst(System.Security.Claims.ClaimTypes.Name)?.Value;
        var role = User.FindFirst(System.Security.Claims.ClaimTypes.Role)?.Value;

        return Ok(new
        {
            message = "Token is valid",
            userId = userId,
            email = email,
            username = username,
            role = role
        });
    }


    [HttpPost("request-otp")]
    public async Task<IActionResult> RequestOtp([FromBody] RequestOtpDto dto)
    {
        // Validate email format
        if (string.IsNullOrWhiteSpace(dto.Email) || !dto.Email.Contains("@"))
            return BadRequest("Invalid email address.");

        // Kiểm tra spam OTP (30 giây)
        var lastOtp = await _emailOtpService.GetOtpAsync(dto.Email);
        if (lastOtp != null && (DateTime.UtcNow - lastOtp.CreatedAt).TotalSeconds < 30)
            return BadRequest("Please wait 30 seconds before requesting another OTP.");

        // Xóa OTP cũ nếu có
        await _emailOtpService.DeleteOtpByEmailAsync(dto.Email);

        // Sinh OTP an toàn bằng Crypto RNG
        var otp = OtpGenerator.GenerateOtp(6);

        // Thời gian hết hạn
        var expires = DateTime.UtcNow.AddMinutes(5);

        // Lưu OTP
        await _emailOtpService.SaveOtpAsync(dto.Email, otp, expires);

        // Nội dung email
        string subject = "Your SpotiXe OTP Code";
        string message = $"Your OTP is: {otp}\nThis code will expire in 5 minutes.";

        // Gửi email
        await _emailService.SendAsync(dto.Email, subject, message);

        return Ok(new { message = "OTP sent successfully." });
    }

    [HttpPost("verify-otp")]
    public async Task<IActionResult> VerifyOtp([FromBody] VerifyOtpDto dto)
    {
        try
        {
            // Validate DTO
            if (string.IsNullOrWhiteSpace(dto.Email) || !dto.Email.Contains("@"))
                return BadRequest("Invalid email.");

            if (string.IsNullOrWhiteSpace(dto.Otp) || dto.Otp.Length < 4)
                return BadRequest("Invalid OTP format.");

            // Lấy OTP từ DB
            var otpRecord = await _emailOtpService.GetOtpAsync(dto.Email);

            if (otpRecord == null)
                return BadRequest("OTP not found. Please request a new OTP.");

            // Kiểm tra OTP hết hạn
            if (otpRecord.ExpiresAt < DateTime.UtcNow)
            {
                await _emailOtpService.DeleteOtpByEmailAsync(dto.Email);
                return BadRequest("OTP expired. Please request a new OTP.");
            }

            // Kiểm tra mã OTP khớp
            if (otpRecord.Otp != dto.Otp)
                return BadRequest("Invalid OTP.");

            // Lấy hoặc tạo user theo Email
            var user = await _userService.FindOrCreateUserByEmailAsync(dto.Email);

            if (user == null)
                return StatusCode(500, "Failed to create user.");

            // Xoá OTP sau khi dùng (chống replay)
            await _emailOtpService.DeleteOtpByEmailAsync(dto.Email);

            // Tạo JWT (UserId + Email + Username)
            var token = _jwtService.GenerateToken(
                user.UserId,
                user.Email,
                user.Username
            );

            return Ok(new
            {
                success = true,
                token,
                user = new
                {
                    user.UserId,
                    user.Email,
                    user.Username
                }
            });
        }
        catch (Exception ex)
        {
            // Bạn có logger thì thêm:
            // _logger.LogError(ex, "Verify OTP failed");
            return StatusCode(500, "Internal server error: " + ex.Message);
        }
    }




}

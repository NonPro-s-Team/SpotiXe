namespace SpotiXeApi.DTOs;

/// <summary>
/// Response trả về khi đăng nhập thành công
/// </summary>
public class LoginResponseDto
{
    /// <summary>
    /// Trạng thái thành công
    /// </summary>
    public bool Success { get; set; }

    /// <summary>
    /// JWT Token để sử dụng trong hệ thống
    /// </summary>
    public string Token { get; set; } = string.Empty;

    /// <summary>
    /// Thời gian hết hạn token (giây)
    /// </summary>
    public int ExpiresIn { get; set; }

    /// <summary>
    /// Thông tin user
    /// </summary>
    public UserDto User { get; set; } = null!;
}

/// <summary>
/// Thông tin user trả về sau khi đăng nhập
/// </summary>
public class UserDto
{
    /// <summary>
    /// ID của user
    /// </summary>
    public long UserId { get; set; }

    /// <summary>
    /// Email của user
    /// </summary>
    public string? Email { get; set; }

    /// <summary>
    /// Số điện thoại của user
    /// </summary>
    public string? Phone { get; set; }

    /// <summary>
    /// Tên người dùng
    /// </summary>
    public string? Username { get; set; }

    /// <summary>
    /// URL avatar
    /// </summary>
    public string? AvatarUrl { get; set; }

    /// <summary>
    /// Firebase UID
    /// </summary>
    public string? FirebaseUid { get; set; }
}

/// <summary>
/// Response lỗi
/// </summary>
public class ErrorResponseDto
{
    /// <summary>
    /// Trạng thái thất bại
    /// </summary>
    public bool Success { get; set; } = false;

    /// <summary>
    /// Thông báo lỗi
    /// </summary>
    public string Message { get; set; } = string.Empty;

    /// <summary>
    /// Chi tiết lỗi (optional)
    /// </summary>
    public string? Details { get; set; }
}

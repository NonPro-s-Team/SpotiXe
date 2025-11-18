using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.IdentityModel.Tokens;

namespace SpotiXeApi.Services;

/// <summary>
/// Service để tạo và xác thực JWT Token
/// </summary>
public class JwtService
{
    private readonly IConfiguration _configuration;
    private readonly string _secretKey;
    private readonly string _issuer;
    private readonly string _audience;
    private readonly int _expireMinutes;

    public JwtService(IConfiguration configuration)
    {
        _configuration = configuration;
        _secretKey = _configuration["Jwt:Key"]
            ?? throw new InvalidOperationException("JWT Key is not configured");
        _issuer = _configuration["Jwt:Issuer"] ?? "spotixe";
        _audience = _configuration["Jwt:Audience"] ?? "spotixe_users";
        _expireMinutes = int.Parse(_configuration["Jwt:ExpireMinutes"] ?? "60");
    }

    /// <summary>
    /// Tạo JWT Token cho user
    /// </summary>
    /// <param name="userId">User ID</param>
    /// <param name="email">Email</param>
    /// <param name="username">Username</param>
    /// <param name="role">Role (mặc định: User)</param>
    /// <returns>JWT Token string</returns>
    public string GenerateToken(long userId, string? email, string? username, string role = "User")
    {
        var securityKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_secretKey));
        var credentials = new SigningCredentials(securityKey, SecurityAlgorithms.HmacSha256);

        // Tạo claims
        var claims = new List<Claim>
        {
            new Claim(JwtRegisteredClaimNames.Sub, userId.ToString()),
            new Claim(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString()),
            new Claim(ClaimTypes.NameIdentifier, userId.ToString()),
            new Claim(ClaimTypes.Role, role)
        };

        // Thêm email nếu có
        if (!string.IsNullOrEmpty(email))
        {
            claims.Add(new Claim(JwtRegisteredClaimNames.Email, email));
            claims.Add(new Claim(ClaimTypes.Email, email));
        }

        // Thêm username nếu có
        if (!string.IsNullOrEmpty(username))
        {
            claims.Add(new Claim(ClaimTypes.Name, username));
        }

        // Tạo token
        var token = new JwtSecurityToken(
            issuer: _issuer,
            audience: _audience,
            claims: claims,
            expires: DateTime.UtcNow.AddMinutes(_expireMinutes),
            signingCredentials: credentials
        );

        return new JwtSecurityTokenHandler().WriteToken(token);
    }

    /// <summary>
    /// Validate JWT Token
    /// </summary>
    /// <param name="token">JWT Token</param>
    /// <returns>ClaimsPrincipal nếu token hợp lệ, null nếu không</returns>
    public ClaimsPrincipal? ValidateToken(string token)
    {
        try
        {
            var tokenHandler = new JwtSecurityTokenHandler();
            var key = Encoding.UTF8.GetBytes(_secretKey);

            var validationParameters = new TokenValidationParameters
            {
                ValidateIssuerSigningKey = true,
                IssuerSigningKey = new SymmetricSecurityKey(key),
                ValidateIssuer = true,
                ValidIssuer = _issuer,
                ValidateAudience = true,
                ValidAudience = _audience,
                ValidateLifetime = true,
                ClockSkew = TimeSpan.Zero
            };

            var principal = tokenHandler.ValidateToken(token, validationParameters, out var validatedToken);
            return principal;
        }
        catch
        {
            return null;
        }
    }

    /// <summary>
    /// Lấy thời gian expire (giây)
    /// </summary>
    public int GetExpireSeconds()
    {
        return _expireMinutes * 60;
    }
}

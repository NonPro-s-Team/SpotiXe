using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using Microsoft.EntityFrameworkCore;
using SpotiXeApi.Context;
using System.Security.Claims;

namespace SpotiXeApi.Filters;

/// <summary>
/// Authorization Filter để kiểm tra user có active không
/// </summary>
public class ActiveUserAuthorizationFilter : IAsyncAuthorizationFilter
{
    private readonly SpotiXeDbContext _context;
    private readonly ILogger<ActiveUserAuthorizationFilter> _logger;

    public ActiveUserAuthorizationFilter(
        SpotiXeDbContext context,
        ILogger<ActiveUserAuthorizationFilter> logger)
    {
        _context = context;
        _logger = logger;
    }

    public async Task OnAuthorizationAsync(AuthorizationFilterContext context)
    {
        // Bỏ qua nếu endpoint có [AllowAnonymous]
        var hasAllowAnonymous = context.ActionDescriptor.EndpointMetadata
            .Any(em => em.GetType() == typeof(Microsoft.AspNetCore.Authorization.AllowAnonymousAttribute));

        if (hasAllowAnonymous)
        {
            return;
        }

        // Kiểm tra user đã authenticated chưa
        if (!context.HttpContext.User.Identity?.IsAuthenticated ?? true)
        {
            return; // Để Authentication middleware xử lý
        }

        // Lấy userId từ claims
        var userIdClaim = context.HttpContext.User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        
        if (string.IsNullOrEmpty(userIdClaim) || !long.TryParse(userIdClaim, out var userId))
        {
            _logger.LogWarning("User authenticated but UserId claim is missing or invalid");
            context.Result = new UnauthorizedObjectResult(new
            {
                message = "Token không hợp lệ."
            });
            return;
        }

        // Kiểm tra user có tồn tại và active không
        var user = await _context.Users
            .AsNoTracking()
            .Where(u => u.UserId == userId)
            .Select(u => new { u.UserId, u.IsActive })
            .FirstOrDefaultAsync();

        if (user == null)
        {
            _logger.LogWarning($"User {userId} not found in database");
            context.Result = new UnauthorizedObjectResult(new
            {
                message = "Tài khoản không tồn tại."
            });
            return;
        }

        if (user.IsActive == 0UL)
        {
            _logger.LogWarning($"Inactive user {userId} attempted to access protected resource");
            context.Result = new UnauthorizedObjectResult(new
            {
                message = "Tài khoản của bạn đã bị vô hiệu hoá."
            });
            return;
        }

        // User active, cho phép request tiếp tục
    }
}

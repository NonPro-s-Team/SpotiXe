using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SpotiXeApi.Context;
using SpotiXeApi.DTOs;
using SpotiXeApi.Entities;
using System.Linq;

namespace SpotiXeApi.Controllers;

[Authorize]
[ApiController]
[Route("api/users")]
public class UsersController : ControllerBase
{
    private readonly SpotiXeDbContext _context;

    public UsersController(SpotiXeDbContext context)
    {
        _context = context;
    }

    [HttpGet]
    public async Task<IActionResult> GetUsers([FromQuery] bool includeDeleted = false, CancellationToken cancellationToken = default)
    {
        IQueryable<User> query = _context.Users.AsNoTracking();
        if (!includeDeleted)
        {
            query = query.Where(x => x.IsActive == 1);
        }
        var results = await query
            .Select(u => new
            {
                u.UserId,
                u.Username,
                u.Email,
                u.PhoneNumber,
                u.FirebaseUid,
                u.AvatarUrl,
                u.IsActive,
                u.CreatedAt,
                u.UpdatedAt,
                u.DeletedAt
            })
            .ToListAsync(cancellationToken);
        return Ok(results);
    }

    [HttpGet("{id:long}")]
    public async Task<IActionResult> GetUserById([FromRoute] long id, CancellationToken cancellationToken = default)
    {
        var user = await _context.Users.AsNoTracking()
            .Where(x => x.UserId == id)
            .Select(u => new
            {
                u.UserId,
                u.Username,
                u.Email,
                u.PhoneNumber,
                u.FirebaseUid,
                u.AvatarUrl,
                u.IsActive,
                u.CreatedAt,
                u.UpdatedAt,
                u.DeletedAt
            })
            .FirstOrDefaultAsync(cancellationToken);
        if (user == null) return NotFound();
        return Ok(user);
    }

    [HttpPost]
    public async Task<IActionResult> CreateUser([FromBody] CreateUserRequest request, CancellationToken cancellationToken = default)
    {
        var userIdHeader = Request.Headers["X-User-Id"].FirstOrDefault();
        var userNameHeader = Request.Headers["X-User-Name"].FirstOrDefault();
        long? userId = long.TryParse(userIdHeader, out var tmp) ? tmp : (long?)null;

        var now = DateTime.UtcNow;
        var entity = new User
        {
            Username = request.Username,
            Email = request.Email,
            PhoneNumber = request.PhoneNumber,
            FirebaseUid = request.FirebaseUid,
            AvatarUrl = request.AvatarUrl,
            IsActive = 1UL,
            CreatedAt = now,
            CreatedById = userId,
            CreatedByName = string.IsNullOrWhiteSpace(userNameHeader) ? null : userNameHeader
        };

        _context.Users.Add(entity);
        await _context.SaveChangesAsync(cancellationToken);

        return CreatedAtAction(nameof(GetUserById), new { id = entity.UserId }, new
        {
            entity.UserId,
            entity.Username,
            entity.Email,
            entity.PhoneNumber,
            entity.FirebaseUid,
            entity.AvatarUrl,
            entity.IsActive,
            entity.CreatedAt,
            entity.UpdatedAt,
            entity.DeletedAt
        });
    }

    [HttpPut("{id:long}")]
    public async Task<IActionResult> UpdateUser([FromRoute] long id, [FromBody] UpdateUserRequest request, CancellationToken cancellationToken = default)
    {
        var entity = await _context.Users.FirstOrDefaultAsync(x => x.UserId == id, cancellationToken);
        if (entity == null) return NotFound();

        if (request.Username != null) entity.Username = request.Username;
        if (request.Email != null) entity.Email = request.Email;
        if (request.PhoneNumber != null) entity.PhoneNumber = request.PhoneNumber;
        if (request.FirebaseUid != null) entity.FirebaseUid = request.FirebaseUid;
        if (request.AvatarUrl != null) entity.AvatarUrl = request.AvatarUrl;

        var userIdHeader = Request.Headers["X-User-Id"].FirstOrDefault();
        var userNameHeader = Request.Headers["X-User-Name"].FirstOrDefault();
        long? userId = long.TryParse(userIdHeader, out var tmp) ? tmp : (long?)null;

        entity.UpdatedAt = DateTime.UtcNow;
        entity.UpdatedById = userId;
        entity.UpdatedByName = string.IsNullOrWhiteSpace(userNameHeader) ? null : userNameHeader;

        await _context.SaveChangesAsync(cancellationToken);
        return NoContent();
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> DeleteUser([FromRoute] long id, CancellationToken cancellationToken = default)
    {
        var entity = await _context.Users.FirstOrDefaultAsync(x => x.UserId == id, cancellationToken);
        if (entity == null) return NotFound();

        if (entity.IsActive == 1)
        {
            entity.IsActive = 0;
            entity.DeletedAt = DateTime.UtcNow;
            await _context.SaveChangesAsync(cancellationToken);
        }

        return NoContent();
    }
}

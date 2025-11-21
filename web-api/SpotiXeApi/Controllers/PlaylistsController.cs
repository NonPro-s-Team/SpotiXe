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
[Route("api/playlists")]
public class PlaylistsController : ControllerBase
{
    private readonly SpotiXeDbContext _context;

    public PlaylistsController(SpotiXeDbContext context)
    {
        _context = context;
    }

    [HttpGet]
    public async Task<IActionResult> GetPlaylists([FromQuery] bool includeDeleted = false, [FromQuery] string? q = null, CancellationToken cancellationToken = default)
    {
        IQueryable<Playlist> query = _context.Playlists.AsNoTracking();
        if (!includeDeleted)
        {
            query = query.Where(x => x.IsActive == 1UL);
        }
        if (!string.IsNullOrWhiteSpace(q))
        {
            var term = q.Trim().ToLower();
            query = query.Where(x => x.Name.ToLower().Contains(term));
        }

        var results = await query
            .Select(p => new
            {
                p.PlaylistId,
                p.Name,
                p.Description,
                p.CoverImageUrl,
                p.OwnerUserId,
                IsPublic = p.IsPublic == null ? (bool?)null : (p.IsPublic == 1UL),
                p.IsActive,
                p.CreatedAt,
                p.UpdatedAt,
                p.DeletedAt
            })
            .ToListAsync(cancellationToken);

        return Ok(results);
    }

    [HttpGet("{id:long}")]
    public async Task<IActionResult> GetPlaylistById([FromRoute] long id, CancellationToken cancellationToken = default)
    {
        var playlist = await _context.Playlists.AsNoTracking()
            .Where(x => x.PlaylistId == id)
            .Select(p => new
            {
                p.PlaylistId,
                p.Name,
                p.Description,
                p.CoverImageUrl,
                p.OwnerUserId,
                IsPublic = p.IsPublic == null ? (bool?)null : (p.IsPublic == 1UL),
                p.IsActive,
                p.CreatedAt,
                p.UpdatedAt,
                p.DeletedAt
            })
            .FirstOrDefaultAsync(cancellationToken);
        if (playlist == null) return NotFound();
        return Ok(playlist);
    }

    [HttpPost]
    public async Task<IActionResult> CreatePlaylist([FromBody] CreatePlaylistRequest request, CancellationToken cancellationToken = default)
    {
        // FK OwnerUserId must exist
        if (!await _context.Users.AnyAsync(u => u.UserId == request.OwnerUserId, cancellationToken))
        {
            return BadRequest(new { message = "OwnerUserId does not exist." });
        }

        var userIdHeader = Request.Headers["X-User-Id"].FirstOrDefault();
        var userNameHeader = Request.Headers["X-User-Name"].FirstOrDefault();
        long? userId = long.TryParse(userIdHeader, out var tmp) ? tmp : (long?)null;

        var now = DateTime.UtcNow;
        var entity = new Playlist
        {
            Name = request.Name,
            Description = request.Description,
            CoverImageUrl = request.CoverImageUrl,
            OwnerUserId = request.OwnerUserId!.Value,
            IsPublic = request.IsPublic.HasValue ? (ulong?)(request.IsPublic.Value ? 1UL : 0UL) : null,
            IsActive = 1UL,
            CreatedAt = now,
            CreatedById = userId,
            CreatedByName = string.IsNullOrWhiteSpace(userNameHeader) ? null : userNameHeader
        };

        _context.Playlists.Add(entity);
        await _context.SaveChangesAsync(cancellationToken);

        return CreatedAtAction(nameof(GetPlaylistById), new { id = entity.PlaylistId }, new
        {
            entity.PlaylistId,
            entity.Name,
            entity.Description,
            entity.CoverImageUrl,
            entity.OwnerUserId,
            IsPublic = entity.IsPublic == null ? (bool?)null : (entity.IsPublic == 1UL),
            entity.IsActive,
            entity.CreatedAt,
            entity.UpdatedAt,
            entity.DeletedAt
        });
    }

    [HttpPut("{id:long}")]
    public async Task<IActionResult> UpdatePlaylist([FromRoute] long id, [FromBody] UpdatePlaylistRequest request, CancellationToken cancellationToken = default)
    {
        var entity = await _context.Playlists.FirstOrDefaultAsync(x => x.PlaylistId == id, cancellationToken);
        if (entity == null) return NotFound();

        if (request.OwnerUserId.HasValue)
        {
            var ownerExists = await _context.Users.AnyAsync(u => u.UserId == request.OwnerUserId.Value, cancellationToken);
            if (!ownerExists)
            {
                return BadRequest(new { message = "OwnerUserId does not exist." });
            }
            entity.OwnerUserId = request.OwnerUserId.Value;
        }

        if (request.Name != null) entity.Name = request.Name;
        if (request.Description != null) entity.Description = request.Description;
        if (request.CoverImageUrl != null) entity.CoverImageUrl = request.CoverImageUrl;
        if (request.IsPublic.HasValue) entity.IsPublic = request.IsPublic.Value ? 1UL : 0UL;

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
    public async Task<IActionResult> DeletePlaylist([FromRoute] long id, CancellationToken cancellationToken = default)
    {
        var entity = await _context.Playlists.FirstOrDefaultAsync(x => x.PlaylistId == id, cancellationToken);
        if (entity == null) return NotFound();
        if (entity.IsActive == 1UL)
        {
            entity.IsActive = 0UL;
            entity.DeletedAt = DateTime.UtcNow;
            await _context.SaveChangesAsync(cancellationToken);
        }
        return NoContent();
    }
}

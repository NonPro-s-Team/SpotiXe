using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SpotiXeApi.Context;
using SpotiXeApi.DTOs;
using SpotiXeApi.Entities;
using System.Linq;

namespace SpotiXeApi.Controllers;

[ApiController]
[Route("api/artists")]
public class ArtistsController : ControllerBase
{
    private readonly SpotiXeDbContext _context;

    public ArtistsController(SpotiXeDbContext context)
    {
        _context = context;
    }

    [HttpGet]
    public async Task<IActionResult> GetArtists([FromQuery] bool includeDeleted = false, [FromQuery] string? q = null, CancellationToken cancellationToken = default)
    {
        IQueryable<Artist> query = _context.Artists.AsNoTracking();
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
            .Select(a => new
            {
                a.ArtistId,
                a.Name,
                a.Bio,
                a.Country,
                a.ProfileImageUrl,
                a.IsActive,
                a.CreatedAt,
                a.UpdatedAt,
                a.DeletedAt
            })
            .ToListAsync(cancellationToken);
        return Ok(results);
    }

    [HttpGet("{id:long}")]
    public async Task<IActionResult> GetArtistById([FromRoute] long id, CancellationToken cancellationToken = default)
    {
        var artist = await _context.Artists.AsNoTracking()
            .Where(x => x.ArtistId == id)
            .Select(a => new
            {
                a.ArtistId,
                a.Name,
                a.Bio,
                a.Country,
                a.ProfileImageUrl,
                a.IsActive,
                a.CreatedAt,
                a.UpdatedAt,
                a.DeletedAt
            })
            .FirstOrDefaultAsync(cancellationToken);
        if (artist == null) return NotFound();
        return Ok(artist);
    }

    [HttpPost]
    public async Task<IActionResult> CreateArtist([FromBody] CreateArtistRequest request, CancellationToken cancellationToken = default)
    {
        var userIdHeader = Request.Headers["X-User-Id"].FirstOrDefault();
        var userNameHeader = Request.Headers["X-User-Name"].FirstOrDefault();
        long? userId = long.TryParse(userIdHeader, out var tmp) ? tmp : (long?)null;

        var now = DateTime.UtcNow;
        var entity = new Artist
        {
            Name = request.Name,
            Bio = request.Bio,
            Country = request.Country,
            ProfileImageUrl = request.ProfileImageUrl,
            IsActive = 1UL,
            CreatedAt = now,
            CreatedById = userId,
            CreatedByName = string.IsNullOrWhiteSpace(userNameHeader) ? null : userNameHeader
        };

        _context.Artists.Add(entity);
        await _context.SaveChangesAsync(cancellationToken);

        return CreatedAtAction(nameof(GetArtistById), new { id = entity.ArtistId }, new
        {
            entity.ArtistId,
            entity.Name,
            entity.Bio,
            entity.Country,
            entity.ProfileImageUrl,
            entity.IsActive,
            entity.CreatedAt,
            entity.UpdatedAt,
            entity.DeletedAt
        });
    }

    [HttpPut("{id:long}")]
    public async Task<IActionResult> UpdateArtist([FromRoute] long id, [FromBody] UpdateArtistRequest request, CancellationToken cancellationToken = default)
    {
        var entity = await _context.Artists.FirstOrDefaultAsync(x => x.ArtistId == id, cancellationToken);
        if (entity == null) return NotFound();

        if (request.Name != null) entity.Name = request.Name;
        if (request.Bio != null) entity.Bio = request.Bio;
        if (request.Country != null) entity.Country = request.Country;
        if (request.ProfileImageUrl != null) entity.ProfileImageUrl = request.ProfileImageUrl;

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
    public async Task<IActionResult> DeleteArtist([FromRoute] long id, CancellationToken cancellationToken = default)
    {
        var entity = await _context.Artists.FirstOrDefaultAsync(x => x.ArtistId == id, cancellationToken);

        if (entity == null) return NotFound();

        if (entity.IsActive == 0UL)
            return NoContent();

        // Soft delete Artist
        entity.IsActive = 0UL;
        entity.DeletedAt = DateTime.UtcNow;

        // Soft delete Albums
        var albums = await _context.Albums
            .Where(a => a.ArtistId == id)
            .ToListAsync(cancellationToken);

        foreach (var album in albums)
        {
            album.IsActive = 0UL;
            album.DeletedAt = DateTime.UtcNow;
        }

        // Soft delete Songs
        var songs = await _context.Songs
            .Where(s => s.ArtistId == id)
            .ToListAsync(cancellationToken);

        foreach (var song in songs)
        {
            song.IsActive = 0UL;
            song.DeletedAt = DateTime.UtcNow;
        }

        // Xóa PlaylistSongs liên kết với các bài hát (xóa cứng)
        // if (songs.Count > 0)
        // {
        //     var songIds = songs.Select(s => s.SongId).ToList();

        //     var playlistSongs = await _context.PlaylistSongs
        //         .Where(ps => songIds.Contains(ps.SongId))
        //         .ToListAsync(cancellationToken);

        //     _context.PlaylistSongs.RemoveRange(playlistSongs);
        // }

        await _context.SaveChangesAsync(cancellationToken);

        return NoContent();
    }
}

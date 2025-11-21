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
[Route("api/songs")]
public class SongsController : ControllerBase
{
    private readonly SpotiXeDbContext _context;

    public SongsController(SpotiXeDbContext context)
    {
        _context = context;
    }

    [HttpGet]
    public async Task<IActionResult> GetSongs([FromQuery] bool includeDeleted = false, [FromQuery] string? q = null, CancellationToken cancellationToken = default)
    {
        IQueryable<Song> query = _context.Songs
            .Include(s => s.Artist)
            .Include(s => s.Album)
            .AsNoTracking();
        if (!includeDeleted)
        {
            query = query.Where(x => x.IsActive == 1UL);
        }
        if (!string.IsNullOrWhiteSpace(q))
        {
            var term = q.Trim().ToLower();
            query = query.Where(x => x.Title.ToLower().Contains(term));
        }

        var results = await query
            .Select(s => new
            {
                s.SongId,
                s.Title,
                s.Duration,
                s.ReleaseDate,
                s.AudioFileUrl,
                s.CoverImageUrl,
                s.Genre,
                s.ArtistId,
                ArtistName = s.Artist.Name,
                s.AlbumId,
                AlbumTitle = s.Album != null ? s.Album.Title : null,
                s.IsActive,
                s.CreatedAt,
                s.UpdatedAt,
                s.DeletedAt
            })
            .ToListAsync(cancellationToken);
        return Ok(results);
    }

    [HttpGet("{id:long}")]
    public async Task<IActionResult> GetSongById([FromRoute] long id, CancellationToken cancellationToken = default)
    {
        var song = await _context.Songs
        .Include(s => s.Artist)
        .Include(s => s.Album)
        .AsNoTracking()
        .Where(x => x.SongId == id)
        .Select(s => new
        {
            s.SongId,
            s.Title,
            s.Duration,
            s.ReleaseDate,
            s.AudioFileUrl,
            s.CoverImageUrl,
            s.Genre,
            s.ArtistId,
            ArtistName = s.Artist.Name,
            s.AlbumId,
            AlbumTitle = s.Album != null ? s.Album.Title : null,
            s.IsActive,
            s.CreatedAt,
            s.UpdatedAt,
            s.DeletedAt
        })
        .FirstOrDefaultAsync(cancellationToken);

        if (song == null) return NotFound();
        return Ok(song);
    }

    [HttpPost]
    public async Task<IActionResult> CreateSong([FromBody] CreateSongRequest request, CancellationToken cancellationToken = default)
    {
        // FK: Artist required
        if (!await _context.Artists.AnyAsync(a => a.ArtistId == request.ArtistId, cancellationToken))
        {
            return BadRequest(new { message = "ArtistId does not exist." });
        }
        // FK: Album optional when provided
        if (request.AlbumId.HasValue)
        {
            var albumExists = await _context.Albums.AnyAsync(a => a.AlbumId == request.AlbumId.Value, cancellationToken);
            if (!albumExists)
            {
                return BadRequest(new { message = "AlbumId does not exist." });
            }
        }

        var userIdHeader = Request.Headers["X-User-Id"].FirstOrDefault();
        var userNameHeader = Request.Headers["X-User-Name"].FirstOrDefault();
        long? userId = long.TryParse(userIdHeader, out var tmp) ? tmp : (long?)null;

        var now = DateTime.UtcNow;
        var entity = new Song
        {
            Title = request.Title,
            Duration = request.Duration,
            ReleaseDate = request.ReleaseDate,
            AudioFileUrl = request.AudioFileUrl,
            CoverImageUrl = request.CoverImageUrl,
            Genre = request.Genre,
            ArtistId = request.ArtistId!.Value,
            AlbumId = request.AlbumId,
            IsActive = 1UL,
            CreatedAt = now,
            CreatedById = userId,
            CreatedByName = string.IsNullOrWhiteSpace(userNameHeader) ? null : userNameHeader
        };

        _context.Songs.Add(entity);
        await _context.SaveChangesAsync(cancellationToken);

        return CreatedAtAction(nameof(GetSongById), new { id = entity.SongId }, new
        {
            entity.SongId,
            entity.Title,
            entity.Duration,
            entity.ReleaseDate,
            entity.AudioFileUrl,
            entity.CoverImageUrl,
            entity.Genre,
            entity.ArtistId,
            entity.AlbumId,
            entity.IsActive,
            entity.CreatedAt,
            entity.UpdatedAt,
            entity.DeletedAt
        });
    }

    [HttpPut("{id:long}")]
    public async Task<IActionResult> UpdateSong([FromRoute] long id, [FromBody] UpdateSongRequest request, CancellationToken cancellationToken = default)
    {
        var entity = await _context.Songs.FirstOrDefaultAsync(x => x.SongId == id, cancellationToken);
        if (entity == null) return NotFound();

        if (request.ArtistId.HasValue)
        {
            var artistExists = await _context.Artists.AnyAsync(a => a.ArtistId == request.ArtistId.Value, cancellationToken);
            if (!artistExists)
            {
                return BadRequest(new { message = "ArtistId does not exist." });
            }
            entity.ArtistId = request.ArtistId.Value;
        }
        if (request.AlbumId.HasValue)
        {
            // Allow setting null by providing AlbumId = null in payload; only validate when HasValue true
            var albumExists = await _context.Albums.AnyAsync(a => a.AlbumId == request.AlbumId.Value, cancellationToken);
            if (!albumExists)
            {
                return BadRequest(new { message = "AlbumId does not exist." });
            }
            entity.AlbumId = request.AlbumId.Value;
        }
        if (request.Title != null) entity.Title = request.Title;
        if (request.Duration.HasValue) entity.Duration = request.Duration;
        if (request.ReleaseDate.HasValue) entity.ReleaseDate = request.ReleaseDate;
        if (request.AudioFileUrl != null) entity.AudioFileUrl = request.AudioFileUrl;
        if (request.CoverImageUrl != null) entity.CoverImageUrl = request.CoverImageUrl;
        if (request.Genre != null) entity.Genre = request.Genre;

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
    public async Task<IActionResult> DeleteSong([FromRoute] long id, CancellationToken cancellationToken = default)
    {
        var entity = await _context.Songs.FirstOrDefaultAsync(x => x.SongId == id, cancellationToken);
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

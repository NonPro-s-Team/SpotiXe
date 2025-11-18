using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SpotiXeApi.Context;
using SpotiXeApi.DTOs;
using SpotiXeApi.Entities;
using System.Linq;

namespace SpotiXeApi.Controllers;

[ApiController]
[Route("api/albums")]
public class AlbumsController : ControllerBase
{
    private readonly SpotiXeDbContext _context;

    public AlbumsController(SpotiXeDbContext context)
    {
        _context = context;
    }

    // GET: api/albums
    [HttpGet]
    public async Task<IActionResult> GetAlbums([FromQuery] bool includeDeleted = false, [FromQuery] string? q = null, CancellationToken cancellationToken = default)
    {
        IQueryable<Album> query = _context.Albums
            .Include(a => a.Artist)
            .AsNoTracking();

        if (!includeDeleted)
        {
            query = query.Where(a => a.IsActive == 1UL);
        }

        if (!string.IsNullOrWhiteSpace(q))
        {
            var term = q.Trim().ToLower();
            query = query.Where(a => a.Title.ToLower().Contains(term));
        }

        var results = await query
            .Select(a => new
            {
                a.AlbumId,
                a.Title,
                a.ArtistId,
                ArtistName = a.Artist.Name,
                a.CoverImageUrl,
                a.ReleaseDate,
                a.IsActive,
                a.CreatedAt,
                a.UpdatedAt,
                a.DeletedAt
            })
            .ToListAsync(cancellationToken);

        return Ok(results);
    }

    // GET: api/albums/{id}
    [HttpGet("{id:long}")]
    public async Task<IActionResult> GetAlbumById([FromRoute] long id, CancellationToken cancellationToken = default)
    {
        var album = await _context.Albums
            .Include(a => a.Artist)
            .AsNoTracking()
            .Where(a => a.AlbumId == id)
            .Select(a => new
            {
                a.AlbumId,
                a.Title,
                a.ArtistId,
                ArtistName = a.Artist.Name,
                a.CoverImageUrl,
                a.ReleaseDate,
                a.IsActive,
                a.CreatedAt,
                a.UpdatedAt,
                a.DeletedAt
            })
            .FirstOrDefaultAsync(cancellationToken);

        if (album == null)
        {
            return NotFound();
        }

        return Ok(album);
    }

    // POST: api/albums
    [HttpPost]
    public async Task<IActionResult> CreateAlbum([FromBody] CreateAlbumRequest request, CancellationToken cancellationToken = default)
    {
        // Model validation handled by [ApiController] automatically

        // FK check: Artist must exist
        if (!await _context.Artists.AnyAsync(x => x.ArtistId == request.ArtistId, cancellationToken))
        {
            return BadRequest(new { message = "ArtistId does not exist." });
        }

        var userIdHeader = Request.Headers["X-User-Id"].FirstOrDefault();
        var userNameHeader = Request.Headers["X-User-Name"].FirstOrDefault();
        long? userId = long.TryParse(userIdHeader, out var tmp) ? tmp : (long?)null;

        var now = DateTime.UtcNow;
        var entity = new Album
        {
            Title = request.Title,
            ArtistId = request.ArtistId!.Value,
            CoverImageUrl = request.CoverImageUrl,
            ReleaseDate = request.ReleaseDate,
            IsActive = 1UL,
            CreatedAt = now,
            CreatedById = userId,
            CreatedByName = string.IsNullOrWhiteSpace(userNameHeader) ? null : userNameHeader
        };

        _context.Albums.Add(entity);
        await _context.SaveChangesAsync(cancellationToken);

        return CreatedAtAction(nameof(GetAlbumById), new { id = entity.AlbumId }, new
        {
            entity.AlbumId,
            entity.Title,
            entity.ArtistId,
            entity.CoverImageUrl,
            entity.ReleaseDate,
            entity.IsActive,
            entity.CreatedAt,
            entity.UpdatedAt,
            entity.DeletedAt
        });
    }

    // PUT: api/albums/{id}
    [HttpPut("{id:long}")]
    public async Task<IActionResult> UpdateAlbum([FromRoute] long id, [FromBody] UpdateAlbumRequest request, CancellationToken cancellationToken = default)
    {
        var entity = await _context.Albums.FirstOrDefaultAsync(a => a.AlbumId == id, cancellationToken);
        if (entity == null)
        {
            return NotFound();
        }

        if (request.ArtistId.HasValue)
        {
            var artistExists = await _context.Artists.AnyAsync(x => x.ArtistId == request.ArtistId.Value, cancellationToken);
            if (!artistExists)
            {
                return BadRequest(new { message = "ArtistId does not exist." });
            }
            entity.ArtistId = request.ArtistId.Value;
        }

        if (request.Title != null) entity.Title = request.Title;
        if (request.CoverImageUrl != null) entity.CoverImageUrl = request.CoverImageUrl;
        if (request.ReleaseDate.HasValue) entity.ReleaseDate = request.ReleaseDate;

        var userIdHeader = Request.Headers["X-User-Id"].FirstOrDefault();
        var userNameHeader = Request.Headers["X-User-Name"].FirstOrDefault();
        long? userId = long.TryParse(userIdHeader, out var tmp) ? tmp : (long?)null;

        entity.UpdatedAt = DateTime.UtcNow;
        entity.UpdatedById = userId;
        entity.UpdatedByName = string.IsNullOrWhiteSpace(userNameHeader) ? null : userNameHeader;

        await _context.SaveChangesAsync(cancellationToken);
        return NoContent();
    }

    // DELETE: api/albums/{id}
    [HttpDelete("{id:long}")]
    public async Task<IActionResult> DeleteAlbum([FromRoute] long id, CancellationToken cancellationToken = default)
    {
        var entity = await _context.Albums.FirstOrDefaultAsync(a => a.AlbumId == id, cancellationToken);
        if (entity == null)
        {
            return NotFound();
        }

        if (entity.IsActive == 1UL)
        {
            entity.IsActive = 0UL;
            entity.DeletedAt = DateTime.UtcNow;
            await _context.SaveChangesAsync(cancellationToken);
        }

        return NoContent();
    }
}

using System;
using System.Collections.Generic;

namespace SpotiXeApi.Entities;

public partial class Song
{
    public long SongId { get; set; }

    public string Title { get; set; } = null!;

    public int? Duration { get; set; }

    public DateOnly? ReleaseDate { get; set; }

    public string? AudioFileUrl { get; set; }

    public string? CoverImageUrl { get; set; }

    public string? Genre { get; set; }

    public long ArtistId { get; set; }

    public long? AlbumId { get; set; }

    public ulong IsActive { get; set; }

    public DateTime? DeletedAt { get; set; }

    public DateTime CreatedAt { get; set; }

    public long? CreatedById { get; set; }

    public string? CreatedByName { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public long? UpdatedById { get; set; }

    public string? UpdatedByName { get; set; }

    public virtual Album? Album { get; set; }

    public virtual Artist Artist { get; set; } = null!;

    public virtual ICollection<Playlistsong> Playlistsongs { get; set; } = new List<Playlistsong>();
}

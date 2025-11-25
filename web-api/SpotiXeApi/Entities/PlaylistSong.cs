using System;
using System.Collections.Generic;

namespace SpotiXeApi.Entities;

public partial class Playlistsong
{
    public long PlaylistId { get; set; }

    public long SongId { get; set; }

    public int? Position { get; set; }

    public DateTime? AddedAt { get; set; }

    public virtual Playlist Playlist { get; set; } = null!;

    public virtual Song Song { get; set; } = null!;
}

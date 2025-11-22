using System;
using System.Collections.Generic;

namespace SpotiXeApi.Entities;

public partial class Userfollowedplaylist
{
    public long UserId { get; set; }

    public long PlaylistId { get; set; }

    public DateTime? FollowedAt { get; set; }

    public virtual Playlist Playlist { get; set; } = null!;

    public virtual User User { get; set; } = null!;
}

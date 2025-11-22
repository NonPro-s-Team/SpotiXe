using System;
using System.Collections.Generic;

namespace SpotiXeApi.Entities;

public partial class Emailotp
{
    public long Id { get; set; }

    public string Email { get; set; } = null!;

    public string Otp { get; set; } = null!;

    public DateTime ExpiresAt { get; set; }

    public DateTime CreatedAt { get; set; }
}

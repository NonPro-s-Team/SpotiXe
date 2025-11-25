using FirebaseAdmin;
using FirebaseAdmin.Auth;
using Google.Apis.Auth.OAuth2;
using Microsoft.EntityFrameworkCore;
using SpotiXeApi.Entities;

namespace SpotiXeApi.Services;

/// <summary>
/// Service để xác thực Firebase ID Token
/// </summary>
public class FirebaseService
{
    private readonly FirebaseAuth _firebaseAuth;

    public FirebaseService()
    {
        // Khởi tạo Firebase Admin SDK nếu chưa có
        if (FirebaseApp.DefaultInstance == null)
        {
            FirebaseApp.Create(new AppOptions()
            {
                Credential = GoogleCredential.GetApplicationDefault(),
            });
        }

        _firebaseAuth = FirebaseAuth.DefaultInstance;
    }

    /// <summary>
    /// Verify Firebase ID Token và trả về thông tin user
    /// </summary>
    /// <param name="idToken">Firebase ID Token từ client</param>
    /// <returns>FirebaseUserInfo nếu token hợp lệ</returns>
    /// <exception cref="FirebaseAuthException">Nếu token không hợp lệ</exception>
    public async Task<FirebaseUserInfo> VerifyIdTokenAsync(string idToken)
    {
        try
        {
            // Verify token với Firebase
            var decodedToken = await _firebaseAuth.VerifyIdTokenAsync(idToken);

            // Lấy thông tin chi tiết của user
            var userRecord = await _firebaseAuth.GetUserAsync(decodedToken.Uid);

            // Map thông tin
            return new FirebaseUserInfo
            {
                Uid = userRecord.Uid,
                Email = userRecord.Email,
                PhoneNumber = userRecord.PhoneNumber,
                DisplayName = userRecord.DisplayName,
                PhotoUrl = userRecord.PhotoUrl,
                EmailVerified = userRecord.EmailVerified,
                Disabled = userRecord.Disabled
            };
        }
        catch (FirebaseAuthException ex)
        {
            throw new UnauthorizedAccessException($"Firebase token verification failed: {ex.Message}", ex);
        }
    }

    /// <summary>
    /// Verify token và trả về UID (lightweight version)
    /// </summary>
    public async Task<string> VerifyAndGetUidAsync(string idToken)
    {
        try
        {
            var decodedToken = await _firebaseAuth.VerifyIdTokenAsync(idToken);
            return decodedToken.Uid;
        }
        catch (FirebaseAuthException ex)
        {
            throw new UnauthorizedAccessException($"Firebase token verification failed: {ex.Message}", ex);
        }
    }

    public async Task DisableFirebaseUser(string firebaseUid)
    {
        await FirebaseAuth.DefaultInstance.UpdateUserAsync(new UserRecordArgs
        {
            Uid = firebaseUid,
            Disabled = true
        });
    }

    public async Task EnableFirebaseUser(string firebaseUid)
    {
        await FirebaseAuth.DefaultInstance.UpdateUserAsync(new UserRecordArgs
        {
            Uid = firebaseUid,
            Disabled = false
        });
    }

    public async Task<UserRecord> GetFirebaseUser(string firebaseUid)
    {
        return await FirebaseAuth.DefaultInstance.GetUserAsync(firebaseUid);
    }

    public async Task<List<UserRecord>> GetAllFirebaseUsersAsync()
    {
        var users = new List<UserRecord>();

        // Bắt đầu paging (mỗi lần Firebase trả tối đa 1000 user)
        var pagedEnumerable = _firebaseAuth.ListUsersAsync(null);

        await foreach (var user in pagedEnumerable)
        {
            users.Add(user);
        }

        return users;
    }

}

/// <summary>
/// Thông tin user từ Firebase
/// </summary>
public class FirebaseUserInfo
{
    /// <summary>
    /// Firebase UID (unique identifier)
    /// </summary>
    public string Uid { get; set; } = string.Empty;

    /// <summary>
    /// Email của user (có thể null với Phone OTP login)
    /// </summary>
    public string? Email { get; set; }

    /// <summary>
    /// Số điện thoại (có thể null với Email/Google login)
    /// </summary>
    public string? PhoneNumber { get; set; }

    /// <summary>
    /// Tên hiển thị từ Firebase
    /// </summary>
    public string? DisplayName { get; set; }

    /// <summary>
    /// URL ảnh đại diện
    /// </summary>
    public string? PhotoUrl { get; set; }

    /// <summary>
    /// Email đã được xác thực chưa
    /// </summary>
    public bool EmailVerified { get; set; }

    /// <summary>
    /// Tài khoản bị vô hiệu hóa không
    /// </summary>
    public bool Disabled { get; set; }
}

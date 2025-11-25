using Microsoft.AspNetCore.Mvc;
using SpotiXeApi.Services;

namespace SpotiXeApi.Controllers
{
    [ApiController]
    [Route("api/admin/firebase")]
    public class AdminFirebaseController : ControllerBase
    {
        private readonly FirebaseService _firebaseService;

        public AdminFirebaseController(FirebaseService firebaseService)
        {
            _firebaseService = firebaseService;
        }

        /// <summary>
        /// Lấy tất cả user Firebase
        /// </summary>
        [HttpGet]
        public async Task<IActionResult> GetFirebaseUsers()
        {
            var users = await _firebaseService.GetAllFirebaseUsersAsync();

            return Ok(new
            {
                success = true,
                count = users.Count,
                data = users.Select(u => new {
                    uid = u.Uid,
                    email = u.Email,
                    phone = u.PhoneNumber,
                    displayName = u.DisplayName,
                    photoUrl = u.PhotoUrl,
                    disabled = u.Disabled,
                    emailVerified = u.EmailVerified
                })
            });
        }

        [HttpPost("disable/{firebaseUid}")]
        public async Task<IActionResult> DisableFirebaseUser(string firebaseUid)
        {
            await _firebaseService.DisableFirebaseUser(firebaseUid);
            return Ok(new { success = true, message = "Firebase user disabled." });
        }

        [HttpPost("enable/{firebaseUid}")]
        public async Task<IActionResult> EnableFirebaseUser(string firebaseUid)
        {
            await _firebaseService.EnableFirebaseUser(firebaseUid);
            return Ok(new { success = true, message = "Firebase user enabled." });
        }

        [HttpGet("{firebaseUid}")]
        public async Task<IActionResult> GetFirebaseUser(string firebaseUid)
        {
            var record = await _firebaseService.GetFirebaseUser(firebaseUid);
            return Ok(record);
        }
    }
}

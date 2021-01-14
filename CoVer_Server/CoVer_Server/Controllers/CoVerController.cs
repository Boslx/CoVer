using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;
using CoVer_Server.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace CoVer_Server.Controllers
{
	[Route("api/")]
	[ApiController]
	public class CoVerController : ControllerBase
	{
		private CoVerDbContext _context;
		private SHA256Managed _sha256;

		public CoVerController(CoVerDbContext context)
		{
			_context = context;
			_sha256 = new SHA256Managed();
		}

		/// <summary>
		/// Returns the secret keys of everyone who has been infected with Covid
		/// </summary>
		/// <response code="200">OK</response>
		[HttpGet]
		[Route("/exposees/")]
		[Produces("application/json")]
		public virtual ActionResult<IEnumerable<Exposee>> GetExposees()
		{
			return StatusCode(200, _context.Exposees);
		}

		/// <summary>
		/// Upload all Secret Keys of a person to inform others about his infection
		/// </summary>
		[HttpPost]
		[Route("/reportInfection/")]
		[Produces("application/json")]
		public virtual ActionResult ReportInfection([FromQuery] [Required()] string validationCode,
			[FromQuery] [Required()] IEnumerable<string> keys)
		{
			var validationcode = _context.ValidationCodes.SingleOrDefault(e => e.Code == validationCode);
			if (validationcode != null)
			{
				if (!validationcode.Valid)
				{
					return StatusCode(403, "Validationcode has already been used!");
				}

				foreach (string key in keys)
				{
					_context.Exposees.Add(new Exposee(key, DateTime.Now));
				}

				validationcode.Valid = false;
				_context.SaveChanges();
				return StatusCode(200);
			}
			else
			{
				return StatusCode(403, "Invalid Validationcode!");
			}
		}

		/// <summary>
		/// Generates a new validation code and stores it in the database
		/// </summary>
		/// <response code="200">OK</response>
		[HttpGet]
		[Route("/newValidationCode/")]
		[Produces("application/json")]
		public virtual ActionResult<byte[]> NewValidationCode([FromQuery] [Required()] string accessToken)
		{
			if (!ValidateUser(accessToken)) return StatusCode(403);
			var result = new ValidationCode();
			_context.ValidationCodes.Add(result);
			_context.SaveChanges();
			return StatusCode(200, result.Code);
		}

		/// <summary>
		/// Deletes exposee from the table.
		/// For development purposes only and must be removed in the final product
		/// </summary>
		[HttpDelete]
		[Route("/deleteAllExposees/")]
		[Produces("application/json")]
		public virtual ActionResult DeleteAllExposees([FromQuery] [Required()] string accessToken)
		{
			if(!ValidateUser(accessToken)) return StatusCode(403);
			_context.Database.ExecuteSqlCommand("TRUNCATE \"Exposees\"");
			return StatusCode(200);
		}

		private bool ValidateUser(string accessToken)
		{
			string accessTokenHash = BitConverter.ToString(_sha256.ComputeHash(Encoding.UTF8.GetBytes(accessToken))).Replace("-", "");
			return _context.AuthorizedUsers.Any(x => x.AccessTokenHash.Equals(accessTokenHash));
		}
	}
}
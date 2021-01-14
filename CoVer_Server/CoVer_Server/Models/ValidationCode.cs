using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;

namespace CoVer_Server.Models
{
	public class ValidationCode
	{
		private static readonly RNGCryptoServiceProvider RngCsp = new RNGCryptoServiceProvider();

		public ValidationCode()
		{
			byte[] randomNumbers = new byte[3];
			lock (RngCsp)
			{
				RngCsp.GetBytes(randomNumbers);
			}

			StringBuilder code = new StringBuilder();
			foreach (byte randomNumber in randomNumbers) code.Append(randomNumber);

			this.Code = code.ToString();
			this.Valid = true;
			this.CreatedOn = DateTime.Now;
		}

		public int Id { get; private set; }
		public bool Valid { get; set; }
		public string Code { get; set; }
		public DateTime CreatedOn { get; private set; }
	}
}

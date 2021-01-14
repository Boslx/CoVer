using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CoVer_Server.Models
{
	public class AuthorizedUser
	{
		public int Id { get; private set; }
		public string Name { get; private set; }
		/// <summary>
		/// SHA-256 encrypted
		/// https://hashgenerator.de/
		/// </summary>
		public string AccessTokenHash { get; private set; }
	}
}

using System;
using System.Collections.Generic;
using System.Text;

namespace CoVer_Server.Models
{
	public class Exposee
	{
		public Exposee(string key, DateTime dateOfRetrieval)
		{
			this.Key = key;
			this.DateOfRetrieval = dateOfRetrieval;
		}

		public int Id { get; private set; }
		public string Key { get; private set; }
		public DateTime DateOfRetrieval { get; private set; }
	}
}

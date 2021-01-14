using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;

namespace CoVer_Server.Models
{
	public class CoVerDbContext:DbContext
	{
		public CoVerDbContext(DbContextOptions<CoVerDbContext> options) : base(options) { }

		public DbSet<Exposee> Exposees { get; set; }
		public DbSet<ValidationCode> ValidationCodes { get; set; }
		public DbSet<AuthorizedUser> AuthorizedUsers { get; set; }
	}
}

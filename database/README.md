# K-Market local database setup

This schema targets the local MariaDB 11.7 installation and is also compatible
with MySQL. It creates the `k_market` database and all tables required by the
current application. It does not drop or overwrite existing data.

## Run from PowerShell

Run this command from the project root:

```powershell
Get-Content -Raw .\database\schema-mysql.sql | & 'C:\Program Files\MariaDB 11.7\bin\mariadb.exe' --user=root --password
```

Enter the local MySQL root password when prompted.

## Application environment variables

Set these in the IntelliJ run configuration, replacing the username and password
with the credentials of the local MySQL account:

```text
DB_URL=jdbc:mysql://localhost:3306/k_market?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
DB_USERNAME=root
DB_PASSWORD=your-local-mysql-password
```

Do not commit a real password to this repository.

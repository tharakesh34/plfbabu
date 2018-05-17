- Set up the environment
1. Create the below system variables.
	- PFF_HOME: The location where the configuration files will be placed. e.g., D:\pennant\PFF\CORE
	- PFF_PASSWORD: The password to decrypt the password. e.g., mysalt123
	- DESIGNER_HOME: The location where the configuration files of process designer will be placed. e.g., D:\pennant\PFF\CORE
2. Specify the data sources in the container. e.g., /PFSWeb/src/main/resources/context.xml for Tomcat.
3. Place the respective configuration file at PFF_HOME.
 
- The audit schema should have access to SecUsers & SecRoles.
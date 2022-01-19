# ldap_authentication
HƯỚNG DẪN CÀI ĐẶT VÀ CHẠY CHƯƠNG TRÌNH
I.	Cài đặt và xây dựng máy chủ LDAP
Cài đặt LDAP trên CentOS 7
-	Cài đặt các gói cần thiết.
yum -y install openldap compat-openldap openldap-clients openldap-servers openldap-servers-sql openldap-deve

•	Khởi động LDAP và cho phép tự động chạy khi khởi động

	systemctl start slapd
	systemctl enable slapd

•	Đặt mật khẩu cho LDAP

	slappasswd

-	Cấu hình OpenLDAP
•	Tạo tập tin db.ldif với nội dung như bên dưới
dn: olcDatabase={2}hdb,cn=config
changetype: modify
replace: olcSuffix
olcSuffix: dc=asm,dc=com
 
dn: olcDatabase={2}hdb,cn=config
changetype: modify
replace: olcRootDN
olcRootDN: cn=Manager,dc=asm,dc=com

dn: olcDatabase={2}hdb,cn=config
changetype: modify
replace: olcRootPW
olcRootPW: hashed_output_from_the_slappasswd_command

•	Triển khai cấu hình vừa tạo
ldapmodify -Y EXTERNAL -H ldapi:/// -f db.ldif

•	Tạo tệp tin monitor.ldif
dn: olcDatabase={1}monitor,cn=config
changetype: modify
replace: olcAccess
olcAccess: {0}to * by dn.base="gidNumber=0+uidNumber=0,cn=peercred,cn=external, cn=auth" read by dn.base="cn=ldapadm,dc=field,dc=linuxhostsupport,dc=com" read by * none

•	Tiếp tục triển khai cấu hình vừa tạo
ldapmodify -Y EXTERNAL -H ldapi:/// -f monitor.ldif

•	Tạo chứng chỉ tự ký và khóa bí mật
openssl req -new -x509 -nodes \
-out  /etc/openldap/certs/asmcert.pem \
-keyout/etc/openldap/certs/asm.pem -days 365

•	Chuyển sở hữu của tập tin chứng chỉ và khóa cho user và group LDAP

chown -R ldap:ldap /etc/openldap/certs

•	Cấu hình OpenLDAP bằng tệp tin certs.ldif để sử dụng LDAPS protocol

dn: cn=config
changetype: modify
replace: olcTLSCertificateKeyFile
olcTLSCertificateKeyFile: /etc/openldap/certs/asmkey.pem

dn: cn=config
changetype: modify
replace: olcTLSCertificateFile
olcTLSCertificateFile: /etc/openldap/certs/asmcert.pem

•	Tiếp tục triển khai cấu hình

ldapmodify -Y EXTERNAL -H ldapi:/// -f certs.ldif

-	Thiết lập cơ sở dữ liệu OpenLDAP
•	Sao chép tệp mẫu cấu hình cơ sở dữ liệu đến /var/lib/ldap và chuyển quyền sở hữu cho ldap

cp /usr/share/openldap-servers/DB_CONFIG.example \ /var/lib/ldap/DB_CONFIG
chown -R ldap:ldap /var/lib/ldap

•	Thêm LDAP schemas

ldapadd -Y EXTERNAL -H ldapi:/// -f \ /etc/openldap/schema/cosine.ldif

ldapadd -Y EXTERNAL -H ldapi:/// -f \ /etc/openldap/schema/nis.ldif

ldapadd -Y EXTERNAL -H ldapi:/// -f \ /etc/openldap/schema/inetorgperson.ldif

•	Tạo tập tin base.ldif  với các thành phần gồm domain, organizationalRole và các organizationalUnit cần thiết

dn: dc=asm,dc=com
dc: asm
objectClass: top
objectClass: domain

dn: cn=Manager,dc=asm,dc=com
objectClass: organizationalRole
cn: Manager
description: LDAP Manager

dn: ou=People,dc=asm,dc=com
objectClass: organizationalUnit
ou: People

dn: ou=Groups,dc=asm,dc=com
objectClass: organizationalUnit
ou: Groups

•	Tiếp tục triển khai cấu hình

ldapadd -x -W -D "cn=Manager,dc=asm,dc=com" -f base.ldif
Xây dựng các objectClass và attributes mới cho LDAP
	Trong quá trình sử dụng LDAP, sẽ có một số thuộc tính của người dùng mà mặc định LDAP không thể hiện. Vì thế để có thể xây dựng theo ý muốn, ta cần thêm các objectClass chứa các thuộc tính mà ta muốn xây dựng. Để làm được điều đó, ta thực hiện như sau.
-	 Tạo tệp tin ldif để cấu hình objectClass chứa thuộc tính mới. 
vim custom.ldif

dn: cn=custom,cn=schema,cn=config
objectClass: olcSchemaConfig
cn: custom
olcAttributeTypes: ( 1.3.6.1.4.1.8876.2.1.10 NAME 'gender' DESC 'RFC2798: male female or unknown, Allowed Values are M F U' SYNTAX 1.3.6.1.4.1.1466.115.121.1.44 SINGLE-VALUE EQUALITY caseExactMatch )
olcAttributeTypes: ( 1.3.6.1.4.1.8876.2.1.6 NAME 'dateOfBirth' EQUALITY generalizedTimeMatch SUBSTR caseIgnoreSubstringsMatch SYNTAX 1.3.6.1.4.1.1466.115.121.1.24 )
olcObjectClasses: ( 2.5.6.24 NAME 'customPerson' DESC 'Custom Person' SUP inetOrgPerson  MAY ( gender $ dateOfBirth ) )

-	Thực hiện cấu hình bằng lệnh: 
ldapadd -Y EXTERNAL -H ldapi:/// -f custom.ldif
-	Tiếp theo, ta tạo schema mới  trong /etc/openldap/schema/
vim custom.schema

attributetype ( 1.3.6.1.4.1.8876.2.1.10 NAME 'gender'
  DESC 'RFC2256: male female or unknown, Allowed Values are M F U'
  SYNTAX 1.3.6.1.4.1.1466.115.121.1.44
  SINGLE-VALUE
  EQUALITY caseExactMatch )

attributetype ( 1.3.6.1.4.1.8876.2.1.6
       NAME 'dateOfBirth'
       EQUALITY generalizedTimeMatch
       SUBSTR caseIgnoreSubstringsMatch
       SYNTAX 1.3.6.1.4.1.1466.115.121.1.24 )

objectclass ( 2.5.6.24 NAME 'customPerson'
   DESC 'Custom Person'
   SUP inetOrgPerson
   MAY ( gender $ dateOfBirth )
)
-	Cuối cùng, khởi độn lại LDAP
systemctl restart slapd

-	Ở hướng dẫn trên, ta đã thực hiện thêm một objectClass chứa các thuộc tính mới là: gender và dateOfBirth.
-	Để thêm các thuộc tính khác, tham khảo các định nghĩa tại: Ldapwiki: Attribute
Kết nối và quản lý bằng LDAP Admin
Để có thể dàng quản lý cây thư mục LDAP một cách trực quan hơn, ta có thể sử dụng LDAP Admin trên Windows

-	Tải và mở LDAP Admin. 
-	Chọn Start -> Connect -> New connection để tạo kết nối đến LDAP server
-	Điền các thông tin của server LDAP và chọn “OK”

 

-	Kiểm tra

 



II.	Chạy chương trình.
Để có thể chạy chương trình, ta cần cài đặt môi trường cho cả back-end và front-end
1.	Cài đặt NodeJs: 
Cài đặt NodeJS trên Windows (openplanning.net)
2.	Cài đặt Java
Cài đặt Java trên Windows (openplanning.net)
3.	Front-end

Phía front-end sử dụng ReactJs. Để chạy, ta vào thư mục chứa dự án chạy lệnh npm -i hoặc npm install để tải thư mục node_modules:
Sau đó nhập lệnh: npm start để chạy.
4.	Back-end
Phía back-end sử dụng Spring Boot. 
Để chạy được, ta cần chỉnh sửa một vài thông số để phù hợp với các server LDAP khác nhau.
Đầu tiên là tập tin application.properties, 
 

Tiếp theo là url trong WebSecurityConfig và dn cho phù hợp.
 

 
Cuối cùng, ta vào thư mục chứa chứ dự án và nhập lệnh ./mvnw spring-boot:run để chạy.



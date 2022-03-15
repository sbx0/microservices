set path=C:\Users\JsonSnow\IdeaProjects\microservices
set now=%date:~0,10%
set now=%now:/=-%

rd /s /q %path%\target\microservices-account
md %path%\target\microservices-account
copy "%path%\microservices-account\target\*.jar" "%path%\target\microservices-account\"
copy "%path%\microservices-account\Dockerfile" "%path%\target\microservices-account\"

rd /s /q %path%\target\microservices-configuration
md %path%\target\microservices-configuration
copy "%path%\microservices-configuration\target\*.jar" "%path%\target\microservices-configuration\"
copy "%path%\microservices-configuration\Dockerfile" "%path%\target\microservices-configuration\"

rd /s /q %path%\target\microservices-gateway
md %path%\target\microservices-gateway
copy "%path%\microservices-gateway\target\*.jar" "%path%\target\microservices-gateway\"
copy "%path%\microservices-gateway\Dockerfile" "%path%\target\microservices-gateway\"

rd /s /q %path%\target\microservices-registry
md %path%\target\microservices-registry
copy "%path%\microservices-registry\target\*.jar" "%path%\target\microservices-registry\"
copy "%path%\microservices-registry\Dockerfile" "%path%\target\microservices-registry\"

rd /s /q %path%\target\microservices-uno
md %path%\target\microservices-uno
copy "%path%\microservices-uno\target\*.jar" "%path%\target\microservices-uno\"
copy "%path%\microservices-uno\Dockerfile" "%path%\target\microservices-uno\"

copy "%path%\docker-compose.yml" "%path%\target\"
copy "%path%\*.sh" "%path%\target\"
rd /s /q %path%\target\configurations
md %path%\target\configurations
copy "%path%\microservices-configuration\src\main\resources\configurations\*" "%path%\target\configurations\"

:: start %path%\target\
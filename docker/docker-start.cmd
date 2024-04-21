for /f "delims=[] tokens=2" %%a in ('ping -4 -n 1 %ComputerName% ^| findstr [') do set NetworkIP=%%a
echo Network IP: %NetworkIP%
set DOCKERHOST=%NetworkIP%
docker-compose -f docker-compose.yml -p kofayoh up -d
docker attach kofayoh-order-status-app-1
cat /etc/nginx/nginx.conf
sed -i 's/SERVER_URL/'"$SERVER_URL"'/g' /etc/nginx/nginx.conf
cat /etc/nginx/nginx.conf
echo "done"
nginx -g "daemon off;"

FROM nginx:stable-alpine
COPY ./config /etc/nginx/nginx.conf
COPY ./build /build
COPY ./start-nginx.sh /start-nginx.sh
WORKDIR /
ENTRYPOINT ./start-nginx.sh

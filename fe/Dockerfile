FROM node:14.17.3-buster as build
WORKDIR /frontend

COPY package.json package.json
COPY package-lock.json package-lock.json

RUN npm ci --production

COPY . .

RUN npm run build


FROM nginx:1.20-alpine as prod

COPY --from=build /frontend/build /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

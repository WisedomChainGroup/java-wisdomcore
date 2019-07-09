FROM node:10-alpine

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm install

COPY *.js ./

CMD ["node", "schedule-monitor.js"]

FROM node:18-alpine
WORKDIR /app
COPY frontend /app
RUN if [ -f package.json ]; then npm install; fi
EXPOSE 3000
CMD ["sh", "-c", "if [ -f package.json ]; then npm start; else echo 'Frontend container started, but no package.json found in /app'; sleep infinity; fi"]

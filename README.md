# Finding Philosophy
Java implementation of “Getting to FindingPhilosophy.Philosophy” (see https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy)

I have Dockerized the app, as that seemed like the most consistent way to share it.
## How to run:
1. Run `./gradlew.bat build docker` on Powershell or `./gradlew build docker` on Unix
2. Make sure Docker is installed
3. To launch the database run
```
docker run -d --name philosophy-mysql -e MYSQL_ROOT_PASSWORD=p4SSW0rd -e MYSQL_DATABASE=db_paths -e MYSQL_USER=springuser -e MYSQL_PASSWORD=ThePassword mysql:latest
```
4. To launch my program run
```
docker run -d  --name finding-philosophy --link philosophy-mysql:mysql -p 8080:8080 -e DATABASE_HOST=philosophy-mysql -e DATABASE_PORT=3306 -e DATABASE_NAME=db_paths findingphilosophy/finding-philosophy
```

## Things I would improve if I had more time:
1. While I am writing to the database, I am not using it as a cache. You can see the database with this command:
```
docker run -it --link philosophy-mysql:mysql --rm mysql sh -c 'exec mysql -h"$MYSQL_PORT_3306_TCP_ADDR" -P"$MYSQL_PORT_3306_TCP_PORT" -uroot -p"$MYSQL_ENV_MYSQL_ROOT_PASSWORD"'
```
2. Create a more responsive site, so instead of waiting until the search is done to load the final page, load it dynamically.
3. Greatly improve the tests. I started strong with some TDD, but as I got into areas that were new to me, I quickly let them slide.
4. Improved error handling. Instead of just using null everywhere I think having real errors would help in a real application.
   - I'd also like to improve the HTTP error handling for nonexistent pages.
5. Refactor the code to be smaller classes, or at least laid out in a more readable fashion.
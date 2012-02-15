/*
 * Copyright (c) 2011 Khanh Tuong Maudoux <kmx.petals@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package fr.soat.devoxx.game.admin.pojo;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import fr.soat.devoxx.game.admin.pojo.exception.StorageException;
import fr.soat.devoxx.game.pojo.question.ResponseType;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: khanh
 * Date: 23/12/11
 * Time: 20:25
 */

@Singleton
public class GameUserDataManager {


    private final Logger LOGGER = LoggerFactory.getLogger(GameUserDataManager.class);
    public final String MONGODB_PROPERTIES = "mongodb.properties";

    private PropertiesConfiguration configuration;

    Datastore ds = null;

    public GameUserDataManager() {
        try {
            this.configuration = new PropertiesConfiguration(MONGODB_PROPERTIES);
        } catch (ConfigurationException e) {
            //NOTHING TO DO
        }

        String host = this.configuration.getString("mongodb.host", "localhost");
        int port = this.configuration.getInt("mongodb.port", 27017);
        String dbName = this.configuration.getString("mongodb.dbname", "devoxx");
        String login = this.configuration.getString("mongodb.login", "");
        String password = this.configuration.getString("mongodb.password", "");

        Mongo mongo = null;
        try {
            mongo = new Mongo(host, port);
            if (!StringUtils.isEmpty(login) || !StringUtils.isEmpty(password)) {
                mongo.getDB(dbName).authenticate(login, password.toCharArray());
            }
            ds = new Morphia().createDatastore(mongo, dbName);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public List<Game> getGamesByResultType(String urlId, ResponseType responseType) {
        List<Game> result = new ArrayList<Game>();
        if (ds != null) {
            List<GameUserData> gameUserDatas = ds.find(GameUserData.class).field("userId").equal(urlId).field("games.type").contains(responseType.name()).asList();
            for (GameUserData gameUserData : gameUserDatas) {
                for (Game game : gameUserData.getGames()) {
                    if (game.getType() == responseType) {
                        result.add(game);
                    }
                }
            }
        } else {
            LOGGER.error("unable to access to mongoDb: datastore is unknown: ");
        }
        return result;
    }

    public Game getGameById(String urlId, int gameId) {
        Game result = null;
        GameUserData gameUserData = null;
        if (ds != null) {
            gameUserData = ds.find(GameUserData.class).field("userId").equal(urlId).field("games.id").equal(gameId).get();
        } else {
            LOGGER.error("unable to access to mongoDb: datastore is unknown: ");
        }
        if (gameUserData != null) {
            Game gameToFind = new Game();
            gameToFind.setId(gameId);
            ArrayList<Game> games = gameUserData.getGames();
            int index = games.indexOf(gameToFind); //cf. hashcode & equals
            if (index != -1) {
                result = games.get(index);
            }
        }
        return result;
    }

    public List<Game> getGames(String urlId) {
        List<Game> result = new ArrayList<Game>();
        GameUserData gameUserData = null;
        if (ds != null) {
            gameUserData = ds.find(GameUserData.class).field("userId").equal(urlId).get();
        } else {
            LOGGER.error("unable to access to mongoDb: datastore is unknown: ");
        }
        if (gameUserData != null) {
            for (Game game : gameUserData.getGames()) {
                result.add(game);
            }
        }
        return result;
    }

    public void registerUser(String urlId) {
        GameUserData gameUserData = new GameUserData();
        gameUserData.setUserId(urlId);
        if (ds != null) {
            ds.save(gameUserData);
            LOGGER.debug("user {} registered in mongoDb", urlId);
        } else {
            LOGGER.error("unable to save user {}: datastore is unknown: ", urlId);
        }
    }

    public void cleanUser(String urlId) {
        if (ds != null) {
            GameUserData entity = ds.get(GameUserData.class, urlId);
            if (entity != null) {
                entity.setGames(new ArrayList<Game>());
                ds.save(entity);
            }
            LOGGER.debug("user {} has been cleanup in mongoDb", urlId);
        } else {
            LOGGER.error("unable to cleanup user {}: datastore is unknown: ", urlId);
        }
    }

    public void destroyUser(String urlId) {
        if (ds != null) {
            GameUserData entity = ds.get(GameUserData.class, urlId);
            if (entity != null) {
                ds.delete(entity);
            }
            LOGGER.debug("user {} is destroyed from mongoDb", urlId);
        } else {
            LOGGER.error("unable to destroy user {}: datastore is unknown: ", urlId);
        }
    }

    public void addOrUpdateGame(String urlId, Game game) throws StorageException {
        if (ds != null) {
            GameUserData gameUserData = ds.find(GameUserData.class).field("userId").equal(urlId).get();
            if (gameUserData == null) {
                LOGGER.error("insertion error: unable to find the user {}... the game {} will not be inserted", urlId, game);
                throw new StorageException("unable to find user " + urlId);
            }
            gameUserData.addOrReplace(game);
            ds.save(gameUserData);
        } else {
            LOGGER.error("unable to save game {} for user {}: datastore is unknown: ", game, urlId);
        }

        //UpdateOperations<User> ops = datastore.createUpdateOperations(User.class).set("lastLogin", now);
//        ds.update(queryToFindMe(), ops);
    }

    public GameResult getResult(String urlId) {
        GameResult result = new GameResult();
        result.setUserId(urlId);
        result.setNbSuccess(getGamesByResultType(urlId, ResponseType.SUCCESS).size());
        result.setNbFail(getGamesByResultType(urlId, ResponseType.FAIL).size());
        result.setNbInvalid(getGamesByResultType(urlId, ResponseType.INVALID).size());
        return result;
    }

    public List<GameResult> getAllResult() {
        List<GameResult> results = new ArrayList<GameResult>();
        for (GameUserData gameUserData : ds.find(GameUserData.class).asList()) {
            results.add(getResult(gameUserData.getUserId()));
        }
        return results;
    }
}

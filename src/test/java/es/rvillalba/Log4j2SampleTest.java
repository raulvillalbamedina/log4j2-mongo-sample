package es.rvillalba;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.bson.Document;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

public class Log4j2SampleTest {

    private static final String COLLECTION_NAME = "testlogs";
    private static final String DB_NAME = "test";
    private static MongoClient mongoClient = null;

    @BeforeClass
    public static void beforeAll() throws UnknownHostException, IOException, URISyntaxException {
        initMongodb();
        selectLog4j2MongoConfiguration();
    }

    private static void selectLog4j2MongoConfiguration() throws URISyntaxException {
        ((org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false)).setConfigLocation(new URI("log4j2-mongo.xml"));
        ((org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false)).reconfigure();
    }

    private static void initMongodb() throws UnknownHostException, IOException {
        int port = 9999;
        IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION).net(new Net(port, Network.localhostIsIPv6())).build();
        MongodStarter runtime = MongodStarter.getDefaultInstance();
        MongodExecutable mongodExecutable = null;
        mongodExecutable = runtime.prepare(mongodConfig);
        mongodExecutable.start();
        mongoClient = new MongoClient("localhost", port);
        DB db = mongoClient.getDB(DB_NAME);
        db.createCollection(COLLECTION_NAME, new BasicDBObject());
    }

    @Test
    public void testLogs() {
        Assert.assertTrue(mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME).count() == 0);
        new Log4j2Sample();
        Assert.assertTrue(mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME).count() == 1);
        FindIterable<Document> results = mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME).find();
        Assert.assertTrue(results.first().toJson().contains(Log4j2Sample.SAMPLE_MESSAGE_MONGO));
    }

    @AfterClass
    public static void afterAll() {
        mongoClient.close();
    }
}

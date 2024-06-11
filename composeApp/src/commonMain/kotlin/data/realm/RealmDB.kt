package data.realm

import domain.model.Message
import domain.model.RealmModel
import domain.model.toMessages
import domain.model.toRealmModel
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class RealmDB {
    private var realm: Realm? = null

    init {
        if (realm == null || realm?.isClosed() == true) {
            val config = RealmConfiguration.Builder(schema = setOf(RealmModel::class))
                .compactOnLaunch()
                .build()
            realm = Realm.open(config)
        }
    }

    suspend fun addMessage(message: Message) {
        realm?.write { copyToRealm(message.toRealmModel()) }
    }

    suspend fun getAllMessages(): List<Message> {
        return realm?.write {
            query(RealmModel::class).find().map {
                it.toMessages()
            }
        } ?: emptyList()
    }

    suspend fun updateMessage(messages: List<Message>) {
        realm?.write {
            val results = this.query(RealmModel::class).find()
            delete(results)

            messages.forEach {
                copyToRealm(it.toRealmModel())
            }
        }

    }
}
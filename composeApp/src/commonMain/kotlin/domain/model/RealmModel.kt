package domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

class RealmModel : RealmObject {
    @PrimaryKey
    var _Id = BsonObjectId()
    var text: String = ""
    var user: String = ""
    var timeStamp: String = ""

}

fun RealmModel.toMessages(): Message {
    return Message(text, user, timeStamp)
}

fun Message.toRealmModel(): RealmModel {
    return RealmModel().apply {
        text = this@toRealmModel.text
        user = this@toRealmModel.user
        timeStamp = this@toRealmModel.timeStamp
    }
}

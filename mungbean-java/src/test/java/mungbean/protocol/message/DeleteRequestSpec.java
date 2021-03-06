/*
   Copyright 2009 Janne Hietamäki

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package mungbean.protocol.message;

import java.io.ByteArrayOutputStream;

import jdave.Specification;
import jdave.junit4.JDaveRunner;
import mungbean.ObjectId;
import mungbean.protocol.DBTransaction;
import mungbean.protocol.bson.MapBSONCoders;
import mungbean.query.Query;

import org.junit.runner.RunWith;

@RunWith(JDaveRunner.class)
public class DeleteRequestSpec extends Specification<DBTransaction<NoResponseExpected>> {
    public class WithAny {
        public DBTransaction<NoResponseExpected> create() {
            return new DBTransaction<NoResponseExpected>(new DeleteRequest("foozbar.foo", new MapBSONCoders(), new Query().field("_id").is(new ObjectId(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 }))), 123);
        }

        public void deleteRequestCanBeSerializedToByteStream() {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            context.sendRequest(output);
            specify(output.toByteArray(), does.containExactly(new byte[] { 58, 0, 0, 0, // message_lenght
                    123, 0, 0, 0, // requestId
                    -1, -1, -1, -1, // responseTo
                    -42, 7, 0, 0, // opCode
                    0, 0, 0, 0, // RESERVED
                    'f', 'o', 'o', 'z', 'b', 'a', 'r', '.', 'f', 'o', 'o', 0, // collectionName
                    0, 0, 0, 0, // RESERVED
                    22, 0, 0, 0, // obj_size
                    7, // element_type = oid
                    '_', 'i', 'd', 0, // name
                    1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, // value
                    0 // eoo
                    }));
        }
    }
}

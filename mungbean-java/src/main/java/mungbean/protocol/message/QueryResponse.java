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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mungbean.protocol.LittleEndianDataReader;
import mungbean.protocol.bson.BSONCoders;
import mungbean.protocol.bson.BSONMap;

public class QueryResponse extends MongoResponse {
	private static final BSONCoders BSON = new BSONCoders();
	private final int responseFlag;
	private final long cursorId;
	private final int startingFrom;
	private final int numberReturned;
	private final List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();

	public QueryResponse(LittleEndianDataReader reader) {
		super(reader);
		responseFlag = reader.readInt();
		cursorId = reader.readLong();
		startingFrom = reader.readInt();
		numberReturned = reader.readInt();
		for (int i = 0; i < numberReturned; i++) {
			values.add(new BSONMap().read(BSON, reader));
		}
	}

	public int responseFlag() {
		return responseFlag;
	}

	public long cursorId() {
		return cursorId;
	}

	public int startingFrom() {
		return startingFrom;
	}

	public int numberReturned() {
		return numberReturned;
	}

	public List<Map<String, Object>> values() {
		return values;
	}
}

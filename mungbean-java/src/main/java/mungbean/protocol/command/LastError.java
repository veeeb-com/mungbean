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

package mungbean.protocol.command;

import java.util.HashMap;
import java.util.Map;

import mungbean.DBCollection;

public class LastError extends Command<String> {
	private final Map<String, Object> parameters = new HashMap<String, Object>() {
		{
			put("getlasterror", 1D);
		}
	};

	@Override
	public String parseResponse(Map<String, Object> values) {
		return (String) values.get("err");
	}

	@Override
	public Map<String, Object> toMap(DBCollection<?> collection) {
		return parameters;
	}
}

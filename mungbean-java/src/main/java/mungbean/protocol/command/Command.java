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

import static mungbean.CollectionUtil.map;

import java.util.Map;

import mungbean.DBCollection;
import mungbean.protocol.message.CommandResponse;
import mungbean.protocol.message.NoResponseExpected;

public class Command extends AbstractCommand<NoResponseExpected> {
	private final String command;

	public Command(String command) {
		this.command = command;
	}

	@Override
	public Map<String, Object> requestMap(DBCollection<?> collection) {
		return map(command, 1D);
	}

	@Override
	public NoResponseExpected parseResponse(CommandResponse values) {
		return new NoResponseExpected();
	}
}

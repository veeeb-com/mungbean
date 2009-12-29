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

package mungbean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mungbean.protocol.DBConnection;
import mungbean.protocol.bson.AbstractBSONCoders;
import mungbean.protocol.bson.BSONCoder;
import mungbean.protocol.bson.BSONCoders;
import mungbean.protocol.command.Command;
import mungbean.protocol.command.LastError;
import mungbean.protocol.message.CommandRequest;
import mungbean.protocol.message.DeleteRequest;
import mungbean.protocol.message.InsertRequest;
import mungbean.protocol.message.QueryOptionsBuilder;
import mungbean.protocol.message.QueryRequest;
import mungbean.protocol.message.UpdateOptionsBuilder;
import mungbean.protocol.message.UpdateRequest;
import mungbean.query.Query;

public abstract class AbstractDBCollection<T> implements DBCollection<T> {
	private static final BSONCoders QUERY_CODERS = new BSONCoders();

	private final DbOperationExecutor executor;
	private final String dbName;
	private final String collectionName;
	private final AbstractBSONCoders coders;

	public AbstractDBCollection(DbOperationExecutor executor, String dbName, String collectionName, AbstractBSONCoders coders) {
		this.executor = executor;
		this.dbName = dbName;
		this.collectionName = collectionName;
		this.coders = coders;
	}

	public CollectionAdmin collectionAdmin() {
		return new CollectionAdmin(this);
	}

	@Override
	public String name() {
		return collectionName;
	}

	public abstract BSONCoder<T> defaultEncoder();

	public T insert(final T doc) {
		return execute(new ErrorCheckingDBConversation() {
			@SuppressWarnings("unchecked")
			@Override
			public T doExecute(DBConnection connection) {
				T newDoc = injectId(doc);
				connection.execute(new InsertRequest<T>(dbName(), coders, newDoc));
				return newDoc;
			}
		});
	}

	protected abstract T injectId(T doc);

	public T update(final ObjectId id, final T doc) {
		return execute(new ErrorCheckingDBConversation() {
			@Override
			public T doExecute(DBConnection connection) {
				connection.execute(new UpdateRequest<T>(dbName(), new UpdateOptionsBuilder(), new HashMap<String, Object>() {
					{
						put("_id", id);
					}
				}, doc, coders, QUERY_CODERS));
				return doc;
			};
		});
	}

	public void delete(Query query) {
		delete(query.build());
	}

	public void delete(final Map<String, Object> query) {
		execute(new ErrorCheckingDBConversation() {
			@Override
			public T doExecute(DBConnection connection) {
				connection.execute(new DeleteRequest(dbName(), QUERY_CODERS, query));
				return null;
			};
		});
	}

	public void update(Query query, T doc, boolean upsert) {
		update(query.build(), doc, upsert);
	}

	public void update(final Map<String, Object> query, final T doc, boolean upsert) {
		final UpdateOptionsBuilder options = new UpdateOptionsBuilder();
		if (upsert) {
			options.upsert().multiUpdate();
		}
		execute(new ErrorCheckingDBConversation() {
			@Override
			public T doExecute(DBConnection connection) {
				connection.execute(new UpdateRequest<T>(dbName(), options, query, doc, coders, QUERY_CODERS));
				return null;
			};
		});
	}

	public List<T> query(Query query, int first, int items) {
		return query(query.build(), first, items);
	}

	public List<T> query(final Map<String, Object> rules, final int first, final int items) {
		final QueryOptionsBuilder options = new QueryOptionsBuilder();
		return execute(new DBConversation<List<T>>() {
			@Override
			public List<T> execute(DBConnection connection) {
				return connection.execute(new QueryRequest<T>(dbName(), options, first, items, true, rules, QUERY_CODERS, defaultEncoder())).values();
			};
		});
	}

	String dbName() {
		return dbName + "." + collectionName;
	}

	public T find(final ObjectId id) {
		List<T> results = query(idQuery(id), 0, 1);
		if (results.isEmpty()) {
			throw new NotFoundException("Item with id " + id + " was not found");
		}
		return results.get(0);
	}

	private HashMap<String, Object> idQuery(final ObjectId id) {
		return new HashMap<String, Object>() {
			{
				put("_id", id);
			}
		};
	}

	public void delete(ObjectId id) {
		delete(idQuery(id));
	}

	@Override
	public <ResponseType> ResponseType command(final Command<ResponseType> command) {
		return execute(new DBConversation<ResponseType>() {
			@Override
			public ResponseType execute(DBConnection connection) {
				return executeCommand(command, connection);
			}
		});
	}

	private <ResponseType> ResponseType executeCommand(Command<ResponseType> command, DBConnection connection) {
		List<Map<String, Object>> result = connection.execute(new CommandRequest(dbName, command.toMap(AbstractDBCollection.this))).values();
		if (result.isEmpty()) {
			throw new NotFoundException("Value not returned for command: " + command);
		}
		Map<String, Object> response = result.get(0);
		if (!response.get("ok").equals(1D)) {
			throw new RuntimeException(response.get("errmsg") + ": " + response.get("bad cmd"));
		}
		return command.parseResponse(response);
	}

	private abstract class ErrorCheckingDBConversation implements DBConversation<T> {
		@Override
		public final T execute(DBConnection connection) {
			T value = doExecute(connection);
			String message = executeCommand(new LastError(), connection);
			if (message != null) {
				throw new MongoException(message);
			}
			return value;
		}

		protected abstract T doExecute(DBConnection connection);
	}

	public <V> V execute(DBConversation<V> conversation) {
		return executor.execute(conversation);
	}
}

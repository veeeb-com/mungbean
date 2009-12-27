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
package mungbean.pojo;

import jdave.Specification;
import jdave.junit4.JDaveRunner;

import mungbean.DBCollection;
import mungbean.Mungbean;
import mungbean.TestObject;

import org.junit.runner.RunWith;

@RunWith(JDaveRunner.class)
public class PojoIntegrationTest extends Specification<DBCollection<TestObject>> {
	public class WithDatabase {
		public DBCollection<TestObject> create() {
			return new Mungbean("localhost", 27017).openDatabase("foobar").openCollection("foo", TestObject.class);
		}

		public void objectWithoutIdCanBeStored() {
			context.insert(new TestObject("foo", 123));
		}

	}
}
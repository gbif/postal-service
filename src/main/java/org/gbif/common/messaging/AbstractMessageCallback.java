/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.common.messaging;

import org.gbif.common.messaging.api.MessageCallback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Implements the {@link MessageCallback} interface and provides a convenience implementation of the
 * {@link #getMessageClass()} method.
 */
public abstract class AbstractMessageCallback<T> implements MessageCallback<T> {

  // Taken from:
  // http://stackoverflow.com/questions/182636/how-to-determine-the-class-of-a-generic-type
  @Override
  public Class<T> getMessageClass() {
    Class<?> superClass = getClass(); // initial value
    Type superType;
    do {
      superType = superClass.getGenericSuperclass();
      superClass = extractClassFromType(superType);
    } while (!superClass.equals(AbstractMessageCallback.class));

    Type actualArg = ((ParameterizedType) superType).getActualTypeArguments()[0];
    return (Class<T>) extractClassFromType(actualArg);
  }

  private Class<?> extractClassFromType(Type t) throws ClassCastException {
    if (t instanceof Class<?>) {
      return (Class<?>) t;
    }
    return (Class<?>) ((ParameterizedType) t).getRawType();
  }
}

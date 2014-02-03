/*
 * Copyright 2014 The FIX.io Project
 *
 * The FIX.io Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package fixio.fixprotocol.fields;


import fixio.fixprotocol.FieldType;
import fixio.fixprotocol.FixMessageFragment;

public abstract class AbstractField<T> extends FixMessageFragment {

    protected AbstractField(int tagNum) {
        super(tagNum);
    }

    public abstract T getValue();

    public abstract byte[] getBytes();

    @Override
    public String toString() {
        int tagNum = getTagNum();
        return FieldType.forTag(tagNum) + "(" + tagNum + ")=" + getValue();
    }
}

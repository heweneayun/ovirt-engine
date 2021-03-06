/*
Copyright (c) 2016 Red Hat, Inc.

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

package org.ovirt.engine.api.v3.adapters;

import org.ovirt.engine.api.model.Status;
import org.ovirt.engine.api.v3.V3Adapter;
import org.ovirt.engine.api.v3.types.V3Status;

public class V3StatusInAdapter implements V3Adapter<V3Status, Status> {
    @Override
    public Status adapt(V3Status from) {
        Status to = new Status();
        if (from.isSetDetail()) {
            to.setDetail(from.getDetail());
        }
        if (from.isSetState()) {
            to.setState(from.getState());
        }
        return to;
    }
}

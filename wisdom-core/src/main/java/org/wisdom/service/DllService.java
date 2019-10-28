package org.wisdom.service;

import org.wisdom.command.Dllparameter;

public interface DllService {

    Object CallMethod(Dllparameter dllparameter);
}

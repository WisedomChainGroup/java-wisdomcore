package org.wisdom.contract;

public interface AnalysisContract extends RLPBeanInterface {

    enum MethodRule {
        CHANGEOWNER, ASSETTRANSFER, INCREASED, MULTTRANSFER, HASHTIMERANSFER, GETHASHTIME,
        HASHHEIGHTRANSFER, GETHASHHEIGHT
    }
}

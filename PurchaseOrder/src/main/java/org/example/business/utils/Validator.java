package org.example.business.utils;

import org.example.errorhandling.customexceptions.IdentifiersMismatchException;

import java.util.UUID;

public class Validator {

    public static void validateIdentifiersMatch(UUID userCompanyUUID, UUID requestCompanyUUID){
        if(!userCompanyUUID.equals(requestCompanyUUID)){
            throw new IdentifiersMismatchException();
        }
    }

}

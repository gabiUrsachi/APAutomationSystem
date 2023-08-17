package org.example.services;

import org.example.Roles;
import org.example.errorhandling.customexceptions.InvalidRoleException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthorisationService {
    public void authorize(Set<Roles> receivedRoles, Roles... validRoles){
        boolean itExistsAtLeastOneValidRole = false;

        for(Roles role:validRoles){
            if(receivedRoles.contains(role)){
                itExistsAtLeastOneValidRole = true;
                break;
            }
        }

        if(!itExistsAtLeastOneValidRole){
            throw new InvalidRoleException();
        }
    }
}

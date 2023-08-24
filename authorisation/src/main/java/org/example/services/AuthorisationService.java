package org.example.services;

import org.example.utils.data.Roles;
import org.example.customexceptions.InvalidRoleException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This service provides methods to authorize user roles against a list of valid roles
 */
@Service
public class AuthorisationService {

    /**
     * Authorizes the received roles against a list of valid roles
     *
     * @param receivedRoles the set of roles received from the user
     * @param validRoles    the array of valid roles to compare against
     * @return a set of authorized roles that are present in both the received and valid roles
     * @throws InvalidRoleException if none of the received roles match the valid roles
     */
    public Set<Roles> authorize(Set<Roles> receivedRoles, Roles... validRoles) {
        Set<Roles> receivedRolesCopy = new HashSet<>(receivedRoles);
        receivedRolesCopy.retainAll(List.of(validRoles));

        if (receivedRolesCopy.size() == 0) {
            throw new InvalidRoleException();
        }

        return receivedRolesCopy;
    }
}

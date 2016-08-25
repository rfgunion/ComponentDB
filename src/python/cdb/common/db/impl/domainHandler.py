#!/usr/bin/env python

from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectAlreadyExists import ObjectAlreadyExists
from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.exceptions.invalidArgument import InvalidArgument
from cdb.common.db.entities.domain import Domain
from cdb.common.db.entities.domainHandler import DomainHandler as DomainHandlerEntity
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler
from cdb.common.db.entities.allowedDomainHandlerEntityType import AllowedDomainHandlerEntityType
from cdb.common.db.impl.entityTypeHandler import EntityTypeHandler

class DomainHandler(CdbDbEntityHandler):

    def __init__(self):
        CdbDbEntityHandler.__init__(self)
        self.entityTypeHandler = EntityTypeHandler()

    def findDomainHandlerByName(self, session, name):
        return self._findDbObjByName(session, DomainHandlerEntity, name)

    def findDomainByName(self, session, name):
        return self._findDbObjByName(session, Domain, name)

    def addDomainHandler(self, session, domainHandlerName, description):
        return self._addSimpleNameDescriptionTable(session, DomainHandlerEntity, domainHandlerName, description)

    def addDomain(self, session, domainName, description, domainHandlerName):
        self._prepareAddUniqueNameObj(session, Domain, domainName)

        # Create Domain Handler
        dbDomain = Domain(name=domainName)
        if description:
            dbDomain.description = description
        if domainHandlerName:
            dbDomain.domainHandler = self.findDomainHandlerByName(session, domainHandlerName)

        session.add(dbDomain)
        session.flush()
        self.logger.debug('Inserted domain id %s' % dbDomain.id)
        return dbDomain

    def addAllowedEntityType(self, session, domainHandlerName, entityTypeName):
        domainHandler = self.findDomainHandlerByName(session, domainHandlerName)
        entityType = self.entityTypeHandler.findEntityTypeByName(session, entityTypeName)

        dbAllowedDomainHandlerEntityType = AllowedDomainHandlerEntityType()

        dbAllowedDomainHandlerEntityType.domainHandler = domainHandler
        dbAllowedDomainHandlerEntityType.entityType = entityType


        session.add(dbAllowedDomainHandlerEntityType)
        session.flush()
        self.logger.debug('Inserted allowed entity type %s for domain handler %s' % (entityTypeName, domainHandlerName))

        return dbAllowedDomainHandlerEntityType
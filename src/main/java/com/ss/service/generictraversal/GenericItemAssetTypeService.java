package com.ss.service.generictraversal;

import com.ss.common.ExecException;
import com.ss.constants.*;
import com.ss.domain.asset.Asset;
import com.ss.domain.asset.AssetType;
import com.ss.domain.asset.AssetTypeProtectedBySce;
import com.ss.domain.asset.AssetTypeToShieldElementMap;
import com.ss.domain.groups.AssetTypeGroup;
import com.ss.domain.groups.AssetTypeGroupMember;
import com.ss.domain.sce.SecurityControlExpression;
import com.ss.domain.shieldclassification.Shield;
import com.ss.domain.shieldclassification.ShieldElement;
import com.ss.domain.usermanagement.OrganizationalUnit;
import com.ss.pojo.PerspectiveGroupInfo;
import com.ss.pojo.ViewDescriptor;
import com.ss.pojo.restservice.GenericItem;
import com.ss.repository.shieldclassification.ShieldRepository;
import com.ss.utils.GenericItemIndexCalculator;
import com.ss.utils.GenericItemPOJOBuilder;
import com.ss.utils.LinkNameHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("GenericItemAssetTypeService")
public class GenericItemAssetTypeService {

    @Autowired
    private GenericItemPOJOBuilder genericItemPOJOBuilder;

    @Autowired
    private GenericItemSceService genericItemSceService;

    @Autowired
    private GenericItemAssetService genericItemAssetService;

    @Autowired
    private GenericItemAssetTypeGroupService genericItemAssetTypeGroupService;

    @Autowired
    private GenericItemOrganizationalUnitService genericItemOrganizationalUnitService;

    @Autowired
    private GenericItemShieldElementService genericItemShieldElementService;

    @Autowired
    private GenericItemIndexCalculator genericItemIndexCalculator;

    @Autowired
    private ShieldRepository shieldRepository;

    @Autowired
    private LinkNameHelper linkNameHelper;

    public GenericItem buildGenericItemForAssetTypeWithProtectedBySceLink(AssetTypeProtectedBySce assetTypeProtectedBySce, AssetType assetType, ViewDescriptor viewDescriptor, PerspectiveGroupInfo perspectiveGroupInfo) {
        if (assetTypeProtectedBySce == null)
            throw new ExecException("buildGenericItemForAssetTypeWithProtectedBySceLink method : assetTypeProtectedBySce parameter is null");
        //GenericItem pojo - special case.
        GenericItem genericItem = genericItemPOJOBuilder.buildGenericPOJO(assetType);
        genericItem.setLinkId(assetTypeProtectedBySce.getId());
        switch (assetTypeProtectedBySce.getShallCouldIs()) {
            case ProtectionType.COULD:
                genericItem.setLinkType(ObjectTypeConstants.ASSET_TYPE_TO_EXPRESSION_LINK_COULD);
                genericItem.setLinkName(LinkName.EXPRESSION_TO_ASSET_TYPE_COULD);
                break;
            /*case ProtectionType.IS:
                genericItem.setLinkTypeAttr(ObjectTypeConstants.ASSET_TYPE_IS_PROTECTED_BY_SCE_LINK);
                genericItem.setLinkNameAttr(LinkName.PROTECTS);
                break;*/
            case ProtectionType.SHALL:
                genericItem.setLinkType(ObjectTypeConstants.ASSET_TYPE_TO_EXPRESSION_LINK);
                genericItem.setLinkName(LinkName.EXPRESSION_TO_ASSET_TYPE);
                break;
            default:
                throw new ExecException("Unknown protection type for asset type " + assetTypeProtectedBySce.getShallCouldIs());
        }

        perspectiveGroupInfo = genericItemPOJOBuilder.handleIsIncludedInGroup(perspectiveGroupInfo, genericItem);

        if (perspectiveGroupInfo.isRated()) {
            switch (perspectiveGroupInfo.getRulerType()) {
                case RulerTypeConstants.ASSET_TYPE_COULD_BE_PROTECTED_BY_SCE:
                    genericItem.setRating(genericItemIndexCalculator.getRatingForAssetTypeCouldBeProtectedBySce(assetTypeProtectedBySce, perspectiveGroupInfo.getPerspectiveIds(), perspectiveGroupInfo.getDate()));
                    break;
                /*case RulerTypeConstants.ASSET_TYPE_IS_PROTECTED_BY_SCE:
                    genericItem.setRating(genericItemIndexCalculator.getRatingForAssetTypeIsProtectedBySce(assetTypeProtectedBySce, perspectiveGroupInfo.getPerspectiveId(), perspectiveGroupInfo.getDate()));
                    break;*/
                case RulerTypeConstants.ASSET_TYPE_SHALL_BE_PROTECTED_BY_SCE:
                    genericItem.setRating(genericItemIndexCalculator.getRatingForAssetTypeShallBeProtectedBySce(assetTypeProtectedBySce, perspectiveGroupInfo.getPerspectiveIds(), perspectiveGroupInfo.getDate()));
                    break;
            }
        }

        handleView(assetType, viewDescriptor, genericItem, perspectiveGroupInfo);
        return genericItem;
    }

    public GenericItem buildGenericItemForAssetTypeToShieldElementMapLink(AssetTypeToShieldElementMap assetTypeToShieldElementMap, AssetType assetType, ViewDescriptor viewDescriptor, PerspectiveGroupInfo perspectiveGroupInfo) {
        if (assetTypeToShieldElementMap == null)
            throw new ExecException("buildGenericItemForAssetTypeToShieldElementMapLink method : assetTypeToShieldElementMap parameter is null");
        //GenericItem pojo - special case.
        GenericItem genericItem = genericItemPOJOBuilder.buildGenericPOJO(assetType);
        genericItem.setLinkId(assetTypeToShieldElementMap.getId());
        genericItem.setLinkType(ObjectTypeConstants.ASSET_TYPE_TO_SHIELD_ELEMENT_LINK);
        genericItem.setLinkName(linkNameHelper.getLinkName(linkNameHelper.getObjectTypeForElement(assetTypeToShieldElementMap.getShieldElement()), ObjectTypeConstants.ASSET_TYPE));

        perspectiveGroupInfo = genericItemPOJOBuilder.handleIsIncludedInGroup(perspectiveGroupInfo, genericItem);

        /*if (perspectiveGroupInfo.isRated()) {
            switch (perspectiveGroupInfo.getRulerType()) {
                case RulerTypeConstants.ASSET_TYPE_COULD_BE_PROTECTED_BY_SCE:
                    genericItem.setRating(genericItemIndexCalculator.getRatingForAssetTypeCouldBeProtectedBySce(assetTypeProtectedBySce, perspectiveGroupInfo.getPerspectiveIds(), perspectiveGroupInfo.getDate()));
                    break;
                *//*case RulerTypeConstants.ASSET_TYPE_IS_PROTECTED_BY_SCE:
                    genericItem.setRating(genericItemIndexCalculator.getRatingForAssetTypeIsProtectedBySce(assetTypeProtectedBySce, perspectiveGroupInfo.getPerspectiveId(), perspectiveGroupInfo.getDate()));
                    break;*//*
                case RulerTypeConstants.ASSET_TYPE_SHALL_BE_PROTECTED_BY_SCE:
                    genericItem.setRating(genericItemIndexCalculator.getRatingForAssetTypeShallBeProtectedBySce(assetTypeProtectedBySce, perspectiveGroupInfo.getPerspectiveIds(), perspectiveGroupInfo.getDate()));
                    break;
            }
        }*/

        handleView(assetType, viewDescriptor, genericItem, perspectiveGroupInfo);
        return genericItem;
    }

    public GenericItem buildGenericItemForAssetType(AssetType assetType, ViewDescriptor viewDescriptor, PerspectiveGroupInfo perspectiveGroupInfo) {
        //GenericItem pojo - special case.
        GenericItem genericItem = genericItemPOJOBuilder.buildGenericPOJO(assetType);

        perspectiveGroupInfo = genericItemPOJOBuilder.handleIsIncludedInGroup(perspectiveGroupInfo, genericItem);

        handleView(assetType, viewDescriptor, genericItem, perspectiveGroupInfo);
        return genericItem;
    }

    private void handleView(AssetType assetType, ViewDescriptor viewDescriptor, GenericItem genericItem, PerspectiveGroupInfo perspectiveGroupInfo) {
        List<GenericItem> children = new ArrayList<>();


        if (viewDescriptor != null && viewDescriptor.getViewName() != null) {
            switch (viewDescriptor.getViewName()) {
                case GIView.ASSET_TYPE:
                    handleViewNameAssetType(assetType, viewDescriptor, children, perspectiveGroupInfo);
                    break;
                case GIView.ASSET:
                    handleViewNameAsset(assetType, viewDescriptor, children, perspectiveGroupInfo);
                    break;
                case GIView.SCE:
                    handleViewNameSce(assetType, viewDescriptor, children, perspectiveGroupInfo);
                    break;
                case GIView.ASSET_TYPE_GROUP:
                    handleViewNameAssetTypeGroup(assetType, viewDescriptor, children, perspectiveGroupInfo);
                    break;
                case GIView.ORGANIZATIONAL_UNIT:
                    handleViewNameOrganizationalUnit(assetType, viewDescriptor, children, perspectiveGroupInfo);
                    break;
                case GIView.DIRECT_SHIELD_ELEMENT:
                    handleViewNameDirectShieldElement(assetType, viewDescriptor, children, perspectiveGroupInfo);
                    break;
                default:
                    throw new ExecException("PerspectiveGenericItemAssetTypeService: unknown viewName " + viewDescriptor.getViewName() + " for AssetType");
            }
        }

        genericItemIndexCalculator.applyGroupSetChildrenAndCalculateIndexFooter(genericItem, perspectiveGroupInfo, children);
    }

    private void handleViewNameDirectShieldElement(AssetType assetType, ViewDescriptor viewDescriptor, List<GenericItem> children, PerspectiveGroupInfo perspectiveGroupInfo) {

        if (viewDescriptor.getSelectionMode() == null || viewDescriptor.getSelectionMode().equals(GIMode.ALL_LINKED_ELEMENTS)) {
            handleAllLinkedCaseForViewDirectShieldElement(assetType, viewDescriptor, children, perspectiveGroupInfo);
        } else if (viewDescriptor.getSelectionMode().equals(GIMode.ALL_LINKED_ELEMENTS_FILTERED_BY_SHIELD_ID)) {
            handleAllLinkedByShieldIdCaseForViewDirectShieldElement(assetType, viewDescriptor, children, perspectiveGroupInfo);
        } else
            throw new ExecException("GenericItemAssetTypeService: handleViewNameDirectShieldElement: viewName: direct_shield_element: unknown selection mode " + viewDescriptor.getSelectionMode());
    }

    private void handleAllLinkedByShieldIdCaseForViewDirectShieldElement(AssetType assetType, ViewDescriptor viewDescriptor, List<GenericItem> children, PerspectiveGroupInfo perspectiveGroupInfo) {
        if (viewDescriptor.getShieldId() == null)
            throw new ExecException("buildGenericItemForAsset: viewName: direct_shield_element: selectionMode : all_linked_by_shield_id : shieldId is null in view descriptor");
        Shield shield = shieldRepository.findOne(viewDescriptor.getShieldId());
        if (shield == null || shield.isArchived())
            throw new ExecException("buildGenericItemForAsset: viewName: direct_shield_element: selectionMode : all_linked_by_shield_id : Shield with id " + viewDescriptor.getShieldId() + " not found");
        int shieldId = shield.getId();

        List<AssetTypeToShieldElementMap> assetTypeToShieldElementMapList = assetType.getAssetTypeToShieldElementMapList();
        if (assetTypeToShieldElementMapList != null && (!assetTypeToShieldElementMapList.isEmpty())) {
            for (AssetTypeToShieldElementMap assetTypeToShieldElementMap : assetTypeToShieldElementMapList) {
                if (assetTypeToShieldElementMap != null && (!assetTypeToShieldElementMap.isArchived())) {
                    ShieldElement shieldElement = assetTypeToShieldElementMap.getShieldElement();
                    if (shieldElement != null && (!shieldElement.isArchived()) && shieldElement.getShield().getId().equals(shieldId))
                        children.add(genericItemShieldElementService.buildGenericItemForAssetTypeToShieldElementMapLink(assetTypeToShieldElementMap, shieldElement, viewDescriptor.getNextLevel(), perspectiveGroupInfo));
                }
            }
        }
    }

    private void handleAllLinkedCaseForViewDirectShieldElement(AssetType assetType, ViewDescriptor viewDescriptor, List<GenericItem> children, PerspectiveGroupInfo perspectiveGroupInfo) {
        List<AssetTypeToShieldElementMap> assetTypeToShieldElementMapList = assetType.getAssetTypeToShieldElementMapList();
        if (assetTypeToShieldElementMapList != null) {
            for (AssetTypeToShieldElementMap assetTypeToShieldElementMap : assetTypeToShieldElementMapList) {
                if (assetTypeToShieldElementMap != null && !assetTypeToShieldElementMap.isArchived()) {
                    ShieldElement shieldElement = assetTypeToShieldElementMap.getShieldElement();
                    if (shieldElement != null && !shieldElement.isArchived())
                        children.add(genericItemShieldElementService.buildGenericItemForAssetTypeToShieldElementMapLink(assetTypeToShieldElementMap, shieldElement, viewDescriptor.getNextLevel(), perspectiveGroupInfo));
                }
            }
        }
    }

    private void handleViewNameAssetTypeGroup(AssetType assetType, ViewDescriptor viewDescriptor, List<GenericItem> children, PerspectiveGroupInfo perspectiveGroupInfo) {
        List<AssetTypeGroupMember> assetTypeGroupMembers = assetType.getAssetTypeGroupMembers();
        if (assetTypeGroupMembers != null && (!assetTypeGroupMembers.isEmpty())) {
            for (AssetTypeGroupMember assetTypeGroupMember : assetTypeGroupMembers) {
                if (assetTypeGroupMember != null && assetTypeGroupMember.isActivated() && (!assetTypeGroupMember.isArchived())) {
                    AssetTypeGroup assetTypeGroup = assetTypeGroupMember.getAssetTypeGroup();
                    if (assetTypeGroup != null && (!assetTypeGroup.isArchived())) {
                        children.add(genericItemAssetTypeGroupService.buildGenericItemForAssetTypeGroup(assetTypeGroup, viewDescriptor.getNextLevel(), perspectiveGroupInfo));
                    }
                }
            }
        }
    }

    private void handleViewNameOrganizationalUnit(AssetType assetType, ViewDescriptor viewDescriptor, List<GenericItem> children, PerspectiveGroupInfo perspectiveGroupInfo) {
        OrganizationalUnit organizationalUnit = assetType.getOrganizationalUnit();
        if (organizationalUnit != null && (!organizationalUnit.isArchived()))
            children.add(genericItemOrganizationalUnitService.buildGenericItemForOrganizationalUnit(organizationalUnit, viewDescriptor.getNextLevel(), perspectiveGroupInfo));
    }

    private void handleViewNameSce(AssetType assetType, ViewDescriptor viewDescriptor, List<GenericItem> children, PerspectiveGroupInfo perspectiveGroupInfo) {
        if (viewDescriptor.getSelectionMode() == null || viewDescriptor.getSelectionMode().equals(GIMode.ALL_LINKED_ELEMENTS)) {
            handleAllLinkedCaseForViewAssetType(assetType, viewDescriptor, children, perspectiveGroupInfo);
        } else if (viewDescriptor.getSelectionMode().equals(GIMode.SHALL_PROTECT)) {
            handleShallProtectCaseForViewAssetType(assetType, viewDescriptor, children, perspectiveGroupInfo);
        } else if (viewDescriptor.getSelectionMode().equals(GIMode.COULD_PROTECT)) {
            handleCouldProtectCaseForViewAssetType(assetType, viewDescriptor, children, perspectiveGroupInfo);
        /*} else if (viewDescriptor.getSelectionMode().equals(GIMode.PROTECT)) {
            handleProtectCaseForViewAssetType(assetType, viewDescriptor, children, perspectiveGroupInfo);*/
        } else
            throw new ExecException("PerspectiveGenericItemAssetTypeService: viewName: asset_type: unknown selection mode " + viewDescriptor.getSelectionMode());
    }

    /*private void handleProtectCaseForViewAssetType(AssetType assetType, ViewDescriptor viewDescriptor, List<GenericItem> children, PerspectiveGroupInfo perspectiveGroupInfo) {
        List<AssetTypeProtectedBySce> assetTypeProtectedBySceList = assetType.getAssetTypeProtectedBySceList();
        if (assetTypeProtectedBySceList != null && (!assetTypeProtectedBySceList.isEmpty())) {
            for (AssetTypeProtectedBySce assetTypeProtectedBySce : assetTypeProtectedBySceList) {
                if (assetTypeProtectedBySce != null && (!assetTypeProtectedBySce.isArchived()) && assetTypeProtectedBySce.getShallCouldIs() != null
                        && assetTypeProtectedBySce.getShallCouldIs().equals(ProtectionType.IS)) {
                    SecurityControlExpression sce = assetTypeProtectedBySce.getSce();
                    if (sce != null && (!sce.isArchived()))
                        children.add(genericItemSceService.buildGenericItemForSceWithAssetTypeProtectedByLink(assetTypeProtectedBySce, sce, viewDescriptor.getNextLevel(), perspectiveGroupInfo));
                }
            }
        }
    }*/

    private void handleCouldProtectCaseForViewAssetType(AssetType assetType, ViewDescriptor viewDescriptor, List<GenericItem> children, PerspectiveGroupInfo perspectiveGroupInfo) {
        List<AssetTypeProtectedBySce> assetTypeProtectedBySceList = assetType.getAssetTypeProtectedBySceList();
        if (assetTypeProtectedBySceList != null && (!assetTypeProtectedBySceList.isEmpty())) {
            for (AssetTypeProtectedBySce assetTypeProtectedBySce : assetTypeProtectedBySceList) {
                if (assetTypeProtectedBySce != null && (!assetTypeProtectedBySce.isArchived()) && assetTypeProtectedBySce.getShallCouldIs() != null
                        && assetTypeProtectedBySce.getShallCouldIs().equals(ProtectionType.COULD)) {
                    SecurityControlExpression sce = assetTypeProtectedBySce.getSce();
                    if (sce != null && (!sce.isArchived()))
                        children.add(genericItemSceService.buildGenericItemForSceWithAssetTypeProtectedByLink(assetTypeProtectedBySce, sce, viewDescriptor.getNextLevel(), perspectiveGroupInfo));
                }
            }
        }
    }

    private void handleShallProtectCaseForViewAssetType(AssetType assetType, ViewDescriptor viewDescriptor, List<GenericItem> children, PerspectiveGroupInfo perspectiveGroupInfo) {
        List<AssetTypeProtectedBySce> assetTypeProtectedBySceList = assetType.getAssetTypeProtectedBySceList();
        if (assetTypeProtectedBySceList != null && (!assetTypeProtectedBySceList.isEmpty())) {
            for (AssetTypeProtectedBySce assetTypeProtectedBySce : assetTypeProtectedBySceList) {
                if (assetTypeProtectedBySce != null && (!assetTypeProtectedBySce.isArchived()) && assetTypeProtectedBySce.getShallCouldIs() != null
                        && assetTypeProtectedBySce.getShallCouldIs().equals(ProtectionType.SHALL)) {
                    SecurityControlExpression sce = assetTypeProtectedBySce.getSce();
                    if (sce != null && (!sce.isArchived()))
                        children.add(genericItemSceService.buildGenericItemForSceWithAssetTypeProtectedByLink(assetTypeProtectedBySce, sce, viewDescriptor.getNextLevel(), perspectiveGroupInfo));
                }
            }
        }
    }

    private void handleAllLinkedCaseForViewAssetType(AssetType assetType, ViewDescriptor viewDescriptor, List<GenericItem> children, PerspectiveGroupInfo perspectiveGroupInfo) {
        List<AssetTypeProtectedBySce> assetTypeProtectedBySceList = assetType.getAssetTypeProtectedBySceList();
        if (assetTypeProtectedBySceList != null && (!assetTypeProtectedBySceList.isEmpty())) {
            for (AssetTypeProtectedBySce assetTypeProtectedBySce : assetTypeProtectedBySceList) {
                if (assetTypeProtectedBySce != null && (!assetTypeProtectedBySce.isArchived())) {
                    SecurityControlExpression sce = assetTypeProtectedBySce.getSce();
                    if (sce != null && (!sce.isArchived()))
                        children.add(genericItemSceService.buildGenericItemForSceWithAssetTypeProtectedByLink(assetTypeProtectedBySce, sce, viewDescriptor.getNextLevel(), perspectiveGroupInfo));
                }
            }
        }
    }

    private void handleViewNameAsset(AssetType assetType, ViewDescriptor viewDescriptor, List<GenericItem> children, PerspectiveGroupInfo perspectiveGroupInfo) {
        List<Asset> assetList = assetType.getAssetList();
        if (assetList != null && (!assetList.isEmpty())) {
            for (Asset asset : assetList) {
                if (asset != null && (!asset.isArchived())) {
                    children.add(genericItemAssetService.buildGenericItemForAsset(asset, viewDescriptor.getNextLevel(), perspectiveGroupInfo));
                }
            }
        }
    }

    private void handleViewNameAssetType(AssetType assetType, ViewDescriptor viewDescriptor, List<GenericItem> children, PerspectiveGroupInfo perspectiveGroupInfo) {
        if (viewDescriptor.getSelectionMode() == null || viewDescriptor.getSelectionMode().equals(GIMode.ALL_LINKED_ELEMENTS) ||
                viewDescriptor.getSelectionMode().equals(GIMode.CHILDREN_ELEMENTS)) {
            handleChildrenCaseForViewAssetType(assetType, viewDescriptor, children, perspectiveGroupInfo);
        } else if (viewDescriptor.getSelectionMode().equals(GIMode.PARENT_ELEMENT)) {
            handleParentCaseForViewAssetType(assetType, viewDescriptor, children, perspectiveGroupInfo);
        } else
            throw new ExecException("PerspectiveGenericItemAssetTypeService: viewName: asset_type: unknown selection mode " + viewDescriptor.getSelectionMode());
    }

    private void handleChildrenCaseForViewAssetType(AssetType assetType, ViewDescriptor viewDescriptor, List<GenericItem> children, PerspectiveGroupInfo perspectiveGroupInfo) {
        List<AssetType> childrenAssetTypeList = assetType.getChildrenAssetTypeList();
        if (childrenAssetTypeList != null && (!childrenAssetTypeList.isEmpty())) {
            for (AssetType childAssetType : childrenAssetTypeList) {
                if (childAssetType != null && (!childAssetType.isArchived()))
                    children.add(buildGenericItemForAssetType(childAssetType, viewDescriptor.getNextLevel(), perspectiveGroupInfo));
            }
        }
    }

    private void handleParentCaseForViewAssetType(AssetType assetType, ViewDescriptor viewDescriptor, List<GenericItem> children, PerspectiveGroupInfo perspectiveGroupInfo) {
        AssetType parentAssetType = assetType.getParentAssetType();
        if (parentAssetType != null && (!parentAssetType.isArchived()))
            children.add(buildGenericItemForAssetType(parentAssetType, viewDescriptor.getNextLevel(), perspectiveGroupInfo));
    }
}
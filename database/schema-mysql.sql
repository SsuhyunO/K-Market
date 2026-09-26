-- K-Market local database schema (MySQL 8.0+)
-- Safe for a new database: this script does not drop existing tables or data.

CREATE DATABASE IF NOT EXISTS `k_market`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `k_market`;

CREATE TABLE IF NOT EXISTS `file` (
  `id` int NOT NULL AUTO_INCREMENT,
  `storedName` varchar(255) NOT NULL,
  `originalName` varchar(255) NOT NULL,
  `path` varchar(255) DEFAULT NULL,
  `extension` varchar(255) DEFAULT NULL,
  `fileSize` bigint NOT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `member` (
  `uid` varchar(20) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `birthDate` varchar(10) DEFAULT NULL,
  `gender` varchar(1) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `memberType` varchar(10) NOT NULL,
  `memberLevel` int DEFAULT NULL,
  `pointBalance` int NOT NULL DEFAULT 0,
  `zipCode` varchar(10) DEFAULT NULL,
  `addr1` varchar(255) DEFAULT NULL,
  `addr2` varchar(255) DEFAULT NULL,
  `regIp` varchar(20) DEFAULT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
  `withdrawnAt` datetime DEFAULT NULL,
  `autoLoginToken` varchar(100) DEFAULT NULL,
  `autoLoginExpireAt` datetime DEFAULT NULL,
  `lastLoginAt` datetime DEFAULT NULL,
  `note` text,
  `provider` varchar(20) NOT NULL DEFAULT 'LOCAL',
  `provider_id` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `policy` (
  `policyType` varchar(255) NOT NULL,
  `content` text,
  PRIMARY KEY (`policyType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `qna` (
  `board_no` int NOT NULL AUTO_INCREMENT,
  `answer` text,
  `answered` bit(1) NOT NULL,
  `answered_at` datetime(6) DEFAULT NULL,
  `category1` varchar(50) NOT NULL,
  `category2` varchar(50) NOT NULL,
  `content` text NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `member_uid` varchar(50) NOT NULL,
  `title` varchar(100) NOT NULL,
  PRIMARY KEY (`board_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `recruit` (
  `id` int NOT NULL AUTO_INCREMENT,
  `department` varchar(255) DEFAULT NULL,
  `experience` varchar(255) DEFAULT NULL,
  `recruitCategory` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `recruitStartAt` datetime DEFAULT NULL,
  `recruitEndAt` datetime DEFAULT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sellerUid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `banner` (
  `bannerId` int NOT NULL AUTO_INCREMENT,
  `bannerType` varchar(255) DEFAULT NULL,
  `fileId` int DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `width` int NOT NULL,
  `height` int NOT NULL,
  `bgColor` varchar(255) DEFAULT NULL,
  `link` varchar(255) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT 0,
  `startAt` datetime NOT NULL,
  `endAt` datetime NOT NULL,
  PRIMARY KEY (`bannerId`),
  KEY `FK_file_TO_banner` (`fileId`),
  CONSTRAINT `FK_file_TO_banner` FOREIGN KEY (`fileId`) REFERENCES `file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `board` (
  `boardNo` int NOT NULL AUTO_INCREMENT,
  `memberUid` varchar(20) NOT NULL,
  `boardType` varchar(255) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `content` varchar(255) DEFAULT NULL,
  `fileId` int DEFAULT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `answer` text,
  `answeredAt` datetime DEFAULT NULL,
  PRIMARY KEY (`boardNo`),
  KEY `FK_member_TO_board` (`memberUid`),
  KEY `FK_file_TO_board` (`fileId`),
  CONSTRAINT `FK_file_TO_board` FOREIGN KEY (`fileId`) REFERENCES `file` (`id`),
  CONSTRAINT `FK_member_TO_board` FOREIGN KEY (`memberUid`) REFERENCES `member` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `board_reply` (
  `replyNo` int NOT NULL AUTO_INCREMENT,
  `boardNo` int NOT NULL,
  `content` text NOT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `rdate` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`replyNo`),
  KEY `FK_board_TO_board_reply` (`boardNo`),
  CONSTRAINT `FK_board_TO_board_reply` FOREIGN KEY (`boardNo`) REFERENCES `board` (`boardNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `category` (
  `cateId` int NOT NULL AUTO_INCREMENT,
  `parentId` int DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `infoNoticeType` varchar(255) DEFAULT NULL,
  `sortOrder` int NOT NULL,
  `code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`cateId`),
  KEY `FK_category_TO_category_1` (`parentId`),
  CONSTRAINT `FK_category_TO_category_1` FOREIGN KEY (`parentId`) REFERENCES `category` (`cateId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `seller` (
  `uid` varchar(20) NOT NULL,
  `companyName` varchar(100) NOT NULL,
  `bizRegNo` varchar(12) NOT NULL,
  `onlineSalesNo` varchar(30) DEFAULT NULL,
  `tel` varchar(20) DEFAULT NULL,
  `fax` varchar(20) DEFAULT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'PENDING',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `uq_seller_companyName` (`companyName`),
  CONSTRAINT `FK_member_TO_seller` FOREIGN KEY (`uid`) REFERENCES `member` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `version` (
  `id` varchar(255) NOT NULL,
  `version` varchar(50) NOT NULL,
  `writerUid` varchar(20) NOT NULL,
  `content` text,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_member_TO_version` (`writerUid`),
  CONSTRAINT `FK_member_TO_version` FOREIGN KEY (`writerUid`) REFERENCES `member` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- The current JPA entity maps to adminConfig (camelCase), not admin_config.
CREATE TABLE IF NOT EXISTS `adminConfig` (
  `id` int NOT NULL AUTO_INCREMENT,
  `bussName` varchar(255) DEFAULT NULL,
  `bussRegNum` varchar(255) DEFAULT NULL,
  `ceo` varchar(255) DEFAULT NULL,
  `copyright` varchar(255) DEFAULT NULL,
  `csBussHours` varchar(255) DEFAULT NULL,
  `csElectronicDisputePhone` varchar(255) DEFAULT NULL,
  `csEmail` varchar(255) DEFAULT NULL,
  `csPhone` varchar(255) DEFAULT NULL,
  `defaultAddr` varchar(255) DEFAULT NULL,
  `detailAddr` varchar(255) DEFAULT NULL,
  `faviconFiled` int NOT NULL,
  `footerLogoFiled` int NOT NULL,
  `headerLogoFiled` int NOT NULL,
  `logoFiled` int NOT NULL,
  `mailOrdBussReg` varchar(255) DEFAULT NULL,
  `mainSliderBannerId` int NOT NULL,
  `mainTopBannerId` int NOT NULL,
  `myPageBannerId` int NOT NULL,
  `prodDetailViewBannerId` int NOT NULL,
  `siteName` varchar(255) DEFAULT NULL,
  `siteSubName` varchar(255) DEFAULT NULL,
  `userLoginBannerId` int NOT NULL,
  `faviconImageId` int NOT NULL,
  `footerLogoImageId` int NOT NULL,
  `headerLogoImageId` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `coupon` (
  `couponNo` int NOT NULL AUTO_INCREMENT,
  `couponType` varchar(255) DEFAULT NULL,
  `sellerUid` varchar(20) DEFAULT NULL,
  `benefit` varchar(255) DEFAULT NULL,
  `issuedCnt` int NOT NULL DEFAULT 0,
  `usedCnt` int NOT NULL DEFAULT 0,
  `status` varchar(255) DEFAULT NULL,
  `notice` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `expireDate` varchar(255) DEFAULT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `startDate` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`couponNo`),
  KEY `FK_seller_TO_coupon` (`sellerUid`),
  CONSTRAINT `FK_seller_TO_coupon` FOREIGN KEY (`sellerUid`) REFERENCES `seller` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `coupon_issue` (
  `issueNo` int NOT NULL AUTO_INCREMENT,
  `couponNo` int NOT NULL,
  `memberUid` varchar(20) NOT NULL,
  `status` int NOT NULL DEFAULT 0,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`issueNo`),
  UNIQUE KEY `uk_coupon_issue_coupon_member` (`couponNo`,`memberUid`),
  KEY `FK_coupon_TO_coupon_issue` (`couponNo`),
  KEY `FK_member_TO_coupon_issue` (`memberUid`),
  CONSTRAINT `FK_coupon_TO_coupon_issue` FOREIGN KEY (`couponNo`) REFERENCES `coupon` (`couponNo`),
  CONSTRAINT `FK_member_TO_coupon_issue` FOREIGN KEY (`memberUid`) REFERENCES `member` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `order` (
  `orderNo` int NOT NULL AUTO_INCREMENT,
  `memberUid` varchar(20) NOT NULL,
  `orderPrice` int NOT NULL,
  `orderDiscount` int NOT NULL DEFAULT 0,
  `couponIssueId` int DEFAULT NULL,
  `orderTotal` int NOT NULL,
  `usedPoints` int NOT NULL DEFAULT 0,
  `receiver` varchar(50) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `zipCode` varchar(10) NOT NULL,
  `addr1` varchar(255) NOT NULL,
  `addr2` varchar(255) DEFAULT NULL,
  `payMethod` varchar(20) NOT NULL,
  `status` varchar(30) NOT NULL,
  `orderNote` text,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`orderNo`),
  KEY `FK_member_TO_order` (`memberUid`),
  KEY `FK_coupon_issue_TO_order_1` (`couponIssueId`),
  CONSTRAINT `FK_coupon_issue_TO_order_1` FOREIGN KEY (`couponIssueId`) REFERENCES `coupon_issue` (`issueNo`),
  CONSTRAINT `FK_member_TO_order` FOREIGN KEY (`memberUid`) REFERENCES `member` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `point` (
  `pointNo` int NOT NULL AUTO_INCREMENT,
  `memberUid` varchar(20) NOT NULL,
  `orderNo` int DEFAULT NULL,
  `point` int NOT NULL DEFAULT 0,
  `content` varchar(255) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expireDate` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`pointNo`),
  KEY `FK_member_TO_point` (`memberUid`),
  KEY `FK_order_TO_point` (`orderNo`),
  CONSTRAINT `FK_member_TO_point` FOREIGN KEY (`memberUid`) REFERENCES `member` (`uid`),
  CONSTRAINT `FK_order_TO_point` FOREIGN KEY (`orderNo`) REFERENCES `order` (`orderNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `product` (
  `prodNo` int NOT NULL AUTO_INCREMENT,
  `cateId` int NOT NULL,
  `prodName` varchar(255) NOT NULL,
  `price` int NOT NULL,
  `discount` int NOT NULL DEFAULT 0,
  `point` int NOT NULL DEFAULT 0,
  `thumb1FileId` int DEFAULT NULL,
  `thumb2FileId` int DEFAULT NULL,
  `thumb3FileId` int DEFAULT NULL,
  `sellerUid` varchar(20) NOT NULL,
  `infoNoticeType` varchar(255) DEFAULT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `detailInfoFileId` int NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `deliveryFee` int NOT NULL,
  `maker` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `hit` int NOT NULL DEFAULT 0,
  `brand` varchar(255) DEFAULT NULL,
  `businessType` varchar(255) DEFAULT NULL,
  `origin` varchar(255) DEFAULT NULL,
  `receiptIssueType` varchar(255) DEFAULT NULL,
  `taxType` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`prodNo`),
  KEY `FK_category_TO_product` (`cateId`),
  KEY `FK_seller_TO_product` (`sellerUid`),
  KEY `FK_file_TO_product_thumb1` (`thumb1FileId`),
  KEY `FK_file_TO_product_thumb2` (`thumb2FileId`),
  KEY `FK_file_TO_product_thumb3` (`thumb3FileId`),
  KEY `FK_file_TO_product_detailInfoFileId` (`detailInfoFileId`),
  CONSTRAINT `FK_category_TO_product` FOREIGN KEY (`cateId`) REFERENCES `category` (`cateId`),
  CONSTRAINT `FK_file_TO_product_detailInfoFileId` FOREIGN KEY (`detailInfoFileId`) REFERENCES `file` (`id`),
  CONSTRAINT `FK_file_TO_product_thumb1` FOREIGN KEY (`thumb1FileId`) REFERENCES `file` (`id`),
  CONSTRAINT `FK_file_TO_product_thumb2` FOREIGN KEY (`thumb2FileId`) REFERENCES `file` (`id`),
  CONSTRAINT `FK_file_TO_product_thumb3` FOREIGN KEY (`thumb3FileId`) REFERENCES `file` (`id`),
  CONSTRAINT `FK_seller_TO_product` FOREIGN KEY (`sellerUid`) REFERENCES `seller` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `product_notice_value` (
  `prodNo` int NOT NULL,
  `noticeKey` varchar(255) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`prodNo`,`noticeKey`),
  CONSTRAINT `FK_product_TO_product_notice_value` FOREIGN KEY (`prodNo`) REFERENCES `product` (`prodNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `product_option_group` (
  `id` int NOT NULL AUTO_INCREMENT,
  `prodNo` int NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `FK_product_TO_product_option_group` (`prodNo`),
  CONSTRAINT `FK_product_TO_product_option_group` FOREIGN KEY (`prodNo`) REFERENCES `product` (`prodNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `product_option_item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `groupId` int NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `FK_product_option_group_TO_product_option_item` (`groupId`),
  CONSTRAINT `FK_product_option_group_TO_product_option_item` FOREIGN KEY (`groupId`) REFERENCES `product_option_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `product_variant` (
  `id` int NOT NULL AUTO_INCREMENT,
  `prodNo` int NOT NULL,
  `stock` int NOT NULL DEFAULT 0,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_product_TO_product_variant` (`prodNo`),
  CONSTRAINT `FK_product_TO_product_variant` FOREIGN KEY (`prodNo`) REFERENCES `product` (`prodNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `product_variant_item` (
  `variantId` int NOT NULL,
  `optionItemId` int NOT NULL,
  PRIMARY KEY (`variantId`,`optionItemId`),
  KEY `FK_product_option_item_TO_product_variant_item` (`optionItemId`),
  CONSTRAINT `FK_product_option_item_TO_product_variant_item` FOREIGN KEY (`optionItemId`) REFERENCES `product_option_item` (`id`),
  CONSTRAINT `FK_product_variant_TO_product_variant_item` FOREIGN KEY (`variantId`) REFERENCES `product_variant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `shipment` (
  `shipmentNo` int NOT NULL AUTO_INCREMENT,
  `orderNo` int NOT NULL,
  `courierName` varchar(50) NOT NULL,
  `trackingNo` varchar(100) NOT NULL,
  `status` varchar(30) NOT NULL DEFAULT 'SHIPPING',
  `shippedAt` datetime DEFAULT NULL,
  `deliveredAt` datetime DEFAULT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sellerUid` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`shipmentNo`),
  UNIQUE KEY `uk_shipment_tracking` (`courierName`,`trackingNo`),
  KEY `idx_shipment_orderNo` (`orderNo`),
  KEY `FK_shipment_TO_seller` (`sellerUid`),
  CONSTRAINT `FK_order_TO_shipment_1` FOREIGN KEY (`orderNo`) REFERENCES `order` (`orderNo`),
  CONSTRAINT `FK_shipment_TO_seller` FOREIGN KEY (`sellerUid`) REFERENCES `seller` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `cart` (
  `cartNo` int NOT NULL AUTO_INCREMENT,
  `memberUid` varchar(20) NOT NULL,
  `prodVariantId` int NOT NULL,
  `count` int NOT NULL DEFAULT 1,
  `price` int NOT NULL,
  `total` int NOT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`cartNo`),
  KEY `FK_member_TO_cart` (`memberUid`),
  KEY `FK_product_variant_TO_cart_1` (`prodVariantId`),
  CONSTRAINT `FK_member_TO_cart` FOREIGN KEY (`memberUid`) REFERENCES `member` (`uid`),
  CONSTRAINT `FK_product_variant_TO_cart_1` FOREIGN KEY (`prodVariantId`) REFERENCES `product_variant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `order_item` (
  `orderItemNo` int NOT NULL AUTO_INCREMENT,
  `orderNo` int NOT NULL,
  `prodVariantId` int NOT NULL,
  `count` int NOT NULL,
  `price` int NOT NULL,
  `total` int NOT NULL,
  `sellerUid` varchar(20) NOT NULL,
  `itemStatus` varchar(30) NOT NULL DEFAULT 'PAID',
  `shippingFee` int NOT NULL,
  `discountRate` int NOT NULL,
  `optionText` varchar(500) DEFAULT NULL,
  `originalPrice` int NOT NULL,
  `prodNo` int NOT NULL,
  `productName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`orderItemNo`),
  KEY `FK_order_TO_order_item` (`orderNo`),
  KEY `FK_product_variant_TO_order_item` (`prodVariantId`),
  KEY `FK_order_item_TO_seller` (`sellerUid`),
  CONSTRAINT `FK_order_item_TO_seller` FOREIGN KEY (`sellerUid`) REFERENCES `seller` (`uid`),
  CONSTRAINT `FK_order_TO_order_item` FOREIGN KEY (`orderNo`) REFERENCES `order` (`orderNo`),
  CONSTRAINT `FK_product_variant_TO_order_item` FOREIGN KEY (`prodVariantId`) REFERENCES `product_variant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `review` (
  `reviewNo` int NOT NULL AUTO_INCREMENT,
  `memberUid` varchar(20) NOT NULL,
  `prodNo` int NOT NULL,
  `rating` int NOT NULL,
  `content` varchar(255) DEFAULT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fileId` int DEFAULT NULL,
  `orderItemNo` int DEFAULT NULL,
  PRIMARY KEY (`reviewNo`),
  UNIQUE KEY `UK_review_orderItemNo` (`orderItemNo`),
  KEY `FK_member_TO_review` (`memberUid`),
  KEY `FK_product_TO_review` (`prodNo`),
  KEY `FK_review_file` (`fileId`),
  CONSTRAINT `FK_member_TO_review` FOREIGN KEY (`memberUid`) REFERENCES `member` (`uid`),
  CONSTRAINT `FK_product_TO_review` FOREIGN KEY (`prodNo`) REFERENCES `product` (`prodNo`),
  CONSTRAINT `FK_review_file` FOREIGN KEY (`fileId`) REFERENCES `file` (`id`),
  CONSTRAINT `FK_order_item_TO_review` FOREIGN KEY (`orderItemNo`) REFERENCES `order_item` (`orderItemNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `shipment_item` (
  `shipmentItemNo` int NOT NULL AUTO_INCREMENT,
  `shipmentNo` int NOT NULL,
  `orderItemNo` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`shipmentItemNo`),
  UNIQUE KEY `uk_shipment_item` (`shipmentNo`,`orderItemNo`),
  KEY `idx_shipment_item_shipmentNo` (`shipmentNo`),
  KEY `idx_shipment_item_orderItemNo` (`orderItemNo`),
  CONSTRAINT `FK_order_item_TO_shipment_item_1` FOREIGN KEY (`orderItemNo`) REFERENCES `order_item` (`orderItemNo`),
  CONSTRAINT `FK_shipment_TO_shipment_item_1` FOREIGN KEY (`shipmentNo`) REFERENCES `shipment` (`shipmentNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `claim` (
  `claimNo` int NOT NULL AUTO_INCREMENT,
  `orderItemNo` int NOT NULL,
  `claimType` varchar(30) DEFAULT NULL,
  `claimContent` varchar(255) DEFAULT NULL,
  `fileId` int DEFAULT NULL,
  `quantity` int NOT NULL DEFAULT 1,
  `claimStatus` varchar(30) NOT NULL DEFAULT 'REQUESTED',
  `orderNo` int NOT NULL,
  `processedAt` datetime(6) DEFAULT NULL,
  `requestedAt` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`claimNo`),
  KEY `FK_file_TO_claim` (`fileId`),
  KEY `FK_orderItem_TO_claim` (`orderItemNo`),
  CONSTRAINT `FK_file_TO_claim` FOREIGN KEY (`fileId`) REFERENCES `file` (`id`),
  CONSTRAINT `FK_orderItem_TO_claim` FOREIGN KEY (`orderItemNo`) REFERENCES `order_item` (`orderItemNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

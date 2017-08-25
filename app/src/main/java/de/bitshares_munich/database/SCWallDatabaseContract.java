package de.bitshares_munich.database;

import android.provider.BaseColumns;

/**
 * Database contract class. Here we define table and column names as constants
 * grouped in their own public static classes.
 * <p>
 * Created by nelson on 12/13/16.
 */
public class SCWallDatabaseContract {

    private SCWallDatabaseContract() {
    }

    public static class BaseTable implements BaseColumns {
        public static final String COLUMN_CREATION_DATE = "creation_date";
    }

    public static class Assets implements BaseColumns {
        public static final String TABLE_NAME = "assets";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_PRECISION = "precision";
        public static final String COLUMN_ISSUER = "issuer";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_MAX_SUPPLY = "max_supply";
    }

    public static class Transfers extends BaseTable {
        public static final String TABLE_NAME = "transfers";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_FEE_AMOUNT = "fee_amount";
        public static final String COLUMN_FEE_ASSET_ID = "fee_asset_id";
        public static final String COLUMN_FROM = "source";
        public static final String COLUMN_TO = "destination";
        public static final String COLUMN_TRANSFER_AMOUNT = "transfer_amount";
        public static final String COLUMN_TRANSFER_ASSET_ID = "transfer_asset_id";
        public static final String COLUMN_MEMO_MESSAGE = "memo";
        public static final String COLUMN_MEMO_FROM = "memo_from_key";
        public static final String COLUMN_MEMO_TO = "memo_to_key";
        public static final String COLUMN_BLOCK_NUM = "block_num";
        public static final String COLUMN_EQUIVALENT_VALUE_ASSET_ID = "equivalent_value_asset_id";
        public static final String COLUMN_EQUIVALENT_VALUE = "equivalent_value";
    }

    public static class UserAccounts extends BaseTable {
        public static final String TABLE_NAME = "user_accounts";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
    }

    public static class AccountKeys extends BaseTable {
        public static final String TABLE_NAME = "account_keys";
        public static final String COLUMN_BRAINKEY = "brainkey";
        public static final String COLUMN_SEQUENCE_NUMBER = "sequence_number";
        public static final String COLUMN_WIF = "wif";
    }
}

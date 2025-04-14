const AWS = require('aws-sdk');
const dynamo = new AWS.DynamoDB.DocumentClient();
const TABLE_NAME = 'subscriptions';

exports.handler = async (event) => {
    const body = JSON.parse(event.body);
    const { user_id, title } = body;

    if (!user_id || !title) {
        return {
            statusCode: 400,
            body: JSON.stringify({ message: 'Missing user_id or title' }),
        };
    }

    const params = {
        TableName: TABLE_NAME,
        Key: {
            user_id,
            title
        }
    };

    try {
        await dynamo.delete(params).promise();
        return {
            statusCode: 200,
            body: JSON.stringify({ message: 'Unsubscribed successfully' }),
        };
    } catch (error) {
        return {
            statusCode: 500,
            body: JSON.stringify({ message: 'Error unsubscribing', error }),
        };
    }
};

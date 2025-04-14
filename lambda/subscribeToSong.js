const AWS = require('aws-sdk');
const dynamo = new AWS.DynamoDB.DocumentClient();
const TABLE_NAME = 'subscriptions';

exports.handler = async (event) => {
    const body = JSON.parse(event.body);
    const { user_id, title, artist } = body;

    if (!user_id || !title || !artist) {
        return {
            statusCode: 400,
            body: JSON.stringify({ message: 'Missing fields' }),
        };
    }

    const params = {
        TableName: TABLE_NAME,
        Item: {
            user_id,
            title,
            artist
        }
    };

    try {
        await dynamo.put(params).promise();
        return {
            statusCode: 200,
            body: JSON.stringify({ message: 'Subscribed successfully' }),
        };
    } catch (error) {
        return {
            statusCode: 500,
            body: JSON.stringify({ message: 'Error subscribing', error }),
        };
    }
};

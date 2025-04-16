const AWS = require('aws-sdk');
const dynamo = new AWS.DynamoDB.DocumentClient();
const TABLE_NAME = process.env.SUBSCRIPTIONS_TABLE || 'subscriptions';

exports.handler = async (event) => {
    console.log("Raw event:", JSON.stringify(event, null, 2));

    // Configure CORS headers
    const corsHeaders = {
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Methods": "DELETE,OPTIONS",
        "Access-Control-Allow-Headers": "Content-Type"
    };

    // Handle OPTIONS preflight request
    if (event.httpMethod === 'OPTIONS') {
        return {
            statusCode: 204,
            headers: corsHeaders,
            body: ''
        };
    }

    // Validate request body existence
    if (!event.body) {
        return {
            statusCode: 400,
            headers: { ...corsHeaders, "Content-Type": "application/json" },
            body: JSON.stringify({ message: "Request body is missing" })
        };
    }

    let body;
    try {
        body = JSON.parse(event.body);
    } catch (e) {
        return {
            statusCode: 400,
            headers: { ...corsHeaders, "Content-Type": "application/json" },
            body: JSON.stringify({ message: "Invalid JSON format" })
        };
    }

    // Validate required fields
    const { user_id, title } = body;
    if (!user_id || !title) {
        return {
            statusCode: 400,
            headers: { ...corsHeaders, "Content-Type": "application/json" },
            body: JSON.stringify({ message: "Missing required fields: user_id or title" })
        };
    }

    try {
        // Delete subscription record
        await dynamo.delete({
            TableName: TABLE_NAME,
            Key: { user_id, title }
        }).promise();

        return {
            statusCode: 200,
            headers: { ...corsHeaders, "Content-Type": "application/json" },
            body: JSON.stringify({
                message: "Unsubscribed successfully",
                data: { title }
            })
        };
    } catch (error) {
        console.error("DynamoDB Error:", error);
        return {
            statusCode: 500,
            headers: { ...corsHeaders, "Content-Type": "application/json" },
            body: JSON.stringify({
                message: "Subscription removal failed",
                error: error.message
            })
        };
    }
};
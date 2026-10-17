const express = require('express');
const router = express.Router();
const productsController = require('../controllers/productsController');
const { verifyToken } = require('../middleware/auth');

// Public endpoints
router.get('/categories', productsController.getCategories);
router.get('/', productsController.getProducts);
router.post('/ai-suggest', productsController.aiSuggest);
router.get('/:id', productsController.getProductById);

// Protected endpoints
router.post('/', verifyToken, productsController.createProduct);
router.post('/:id/bid', verifyToken, productsController.placeBid);

module.exports = router;

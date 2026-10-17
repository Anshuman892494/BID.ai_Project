const { v4: uuidv4 } = require('uuid');
const { getPool, isMemoryFallback, memoryDb } = require('../db');

// Get categories
exports.getCategories = async (req, res) => {
  try {
    if (!isMemoryFallback()) {
      const pool = getPool();
      const result = await pool.query('SELECT * FROM categories ORDER BY id ASC');
      return res.status(200).json({ status: 'success', data: result.rows });
    }
    return res.status(200).json({ status: 'success', data: memoryDb.categories });
  } catch (err) {
    console.error('Error fetching categories:', err);
    return res.status(500).json({ status: 'error', message: 'Failed to fetch categories' });
  }
};

// Get all products (with category filter, search query, and sorting)
exports.getProducts = async (req, res) => {
  try {
    const { category, search, sort } = req.query;

    if (!isMemoryFallback()) {
      const pool = getPool();
      let query = `
        SELECT p.*, c.name as category_name, u.full_name as seller_name,
               COALESCE((SELECT COUNT(*) FROM bids WHERE product_id = p.id), 0) as bids_count
        FROM products p
        LEFT JOIN categories c ON p.category_id = c.id
        LEFT JOIN users u ON p.seller_id = u.id
        WHERE p.status = 'ACTIVE'
      `;
      const params = [];

      if (category && category !== 'All' && category !== '1') {
        params.push(isNaN(category) ? category : parseInt(category));
        query += isNaN(category) 
          ? ` AND LOWER(c.name) = LOWER($${params.length})`
          : ` AND p.category_id = $${params.length}`;
      }

      if (search && search.trim() !== '') {
        params.push(`%${search.trim().toLowerCase()}%`);
        query += ` AND (LOWER(p.title) LIKE $${params.length} OR LOWER(p.description) LIKE $${params.length})`;
      }

      if (sort === 'endingSoon') {
        query += ' ORDER BY p.ends_at ASC';
      } else if (sort === 'priceLow') {
        query += ' ORDER BY p.current_bid ASC';
      } else if (sort === 'priceHigh') {
        query += ' ORDER BY p.current_bid DESC';
      } else {
        query += ' ORDER BY p.created_at DESC';
      }

      const result = await pool.query(query, params);
      return res.status(200).json({
        status: 'success',
        count: result.rows.length,
        data: result.rows
      });
    } else {
      // In-Memory search & filter
      let items = [...memoryDb.products];

      if (category && category !== 'All' && category !== '1') {
        if (!isNaN(category)) {
          items = items.filter(p => p.category_id === parseInt(category));
        } else {
          const cat = memoryDb.categories.find(c => c.name.toLowerCase() === category.toLowerCase());
          if (cat) {
            items = items.filter(p => p.category_id === cat.id);
          }
        }
      }

      if (search && search.trim() !== '') {
        const queryTerm = search.trim().toLowerCase();
        items = items.filter(p => 
          p.title.toLowerCase().includes(queryTerm) || 
          p.description.toLowerCase().includes(queryTerm)
        );
      }

      // Populate category and seller names
      const enriched = items.map(p => {
        const cat = memoryDb.categories.find(c => c.id === p.category_id);
        const seller = memoryDb.users.find(u => u.id === p.seller_id);
        return {
          ...p,
          category_name: cat ? cat.name : 'General',
          seller_name: seller ? seller.full_name : 'Verified Seller'
        };
      });

      if (sort === 'endingSoon') {
        enriched.sort((a, b) => new Date(a.ends_at) - new Date(b.ends_at));
      } else if (sort === 'priceLow') {
        enriched.sort((a, b) => a.current_bid - b.current_bid);
      } else if (sort === 'priceHigh') {
        enriched.sort((a, b) => b.current_bid - a.current_bid);
      } else {
        enriched.sort((a, b) => new Date(b.created_at) - new Date(a.created_at));
      }

      return res.status(200).json({
        status: 'success',
        count: enriched.length,
        data: enriched
      });
    }
  } catch (err) {
    console.error('Error in getProducts:', err);
    return res.status(500).json({ status: 'error', message: 'Failed to fetch products' });
  }
};

// Get single product details
exports.getProductById = async (req, res) => {
  try {
    const { id } = req.params;

    if (!isMemoryFallback()) {
      const pool = getPool();
      const productResult = await pool.query(
        `SELECT p.*, c.name as category_name, u.full_name as seller_name, u.eco_score as seller_eco_score
         FROM products p
         LEFT JOIN categories c ON p.category_id = c.id
         LEFT JOIN users u ON p.seller_id = u.id
         WHERE p.id = $1`,
        [id]
      );

      if (productResult.rows.length === 0) {
        return res.status(404).json({ status: 'error', message: 'Product not found' });
      }

      const bidsResult = await pool.query(
        `SELECT b.*, u.full_name as bidder_name
         FROM bids b
         LEFT JOIN users u ON b.bidder_id = u.id
         WHERE b.product_id = $1
         ORDER BY b.created_at DESC`,
        [id]
      );

      return res.status(200).json({
        status: 'success',
        data: {
          ...productResult.rows[0],
          bids: bidsResult.rows
        }
      });
    } else {
      const product = memoryDb.products.find(p => p.id === id);
      if (!product) {
        return res.status(404).json({ status: 'error', message: 'Product not found' });
      }

      const cat = memoryDb.categories.find(c => c.id === product.category_id);
      const seller = memoryDb.users.find(u => u.id === product.seller_id);
      const bids = memoryDb.bids.filter(b => b.product_id === id).sort((a, b) => new Date(b.created_at) - new Date(a.created_at));

      return res.status(200).json({
        status: 'success',
        data: {
          ...product,
          category_name: cat ? cat.name : 'General',
          seller_name: seller ? seller.full_name : 'Verified Seller',
          seller_eco_score: seller ? seller.eco_score : 100,
          bids
        }
      });
    }
  } catch (err) {
    console.error('Error fetching product by ID:', err);
    return res.status(500).json({ status: 'error', message: 'Failed to fetch product details' });
  }
};

// Create Product - "One-Click Sell" submission
exports.createProduct = async (req, res) => {
  try {
    const sellerId = req.user.id;
    const {
      title,
      categoryId,
      description,
      startingPrice,
      condition,
      imageUrl,
      estimatedEcoScore,
      durationHours
    } = req.body;

    if (!title || !startingPrice) {
      return res.status(400).json({
        status: 'error',
        message: 'Title and startingPrice are required.'
      });
    }

    const price = parseFloat(startingPrice);
    const catId = categoryId ? parseInt(categoryId) : 2; // Default to Mobiles/Electronics
    const hours = durationHours ? parseInt(durationHours) : 24;
    const endsAt = new Date(Date.now() + hours * 3600 * 1000);
    const ecoScore = estimatedEcoScore ? parseInt(estimatedEcoScore) : Math.floor(Math.random() * 40) + 50;

    const fallbackImages = [
      'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=600&q=80',
      'https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?w=600&q=80',
      'https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=600&q=80'
    ];
    const finalImage = imageUrl && imageUrl.trim() !== '' 
      ? imageUrl 
      : fallbackImages[Math.floor(Math.random() * fallbackImages.length)];

    if (!isMemoryFallback()) {
      const pool = getPool();
      const insertResult = await pool.query(
        `INSERT INTO products 
         (seller_id, category_id, title, description, starting_price, current_bid, condition, image_url, estimated_eco_score, status, ends_at)
         VALUES ($1, $2, $3, $4, $5, $5, $6, $7, $8, 'ACTIVE', $9)
         RETURNING *`,
        [sellerId, catId, title.trim(), description || '', price, condition || 'Good', finalImage, ecoScore, endsAt]
      );

      // Increment seller eco-score
      await pool.query('UPDATE users SET eco_score = eco_score + $1 WHERE id = $2', [ecoScore, sellerId]);

      return res.status(201).json({
        status: 'success',
        message: 'Product listed successfully for auction!',
        data: insertResult.rows[0]
      });
    } else {
      const newProduct = {
        id: uuidv4(),
        seller_id: sellerId,
        category_id: catId,
        title: title.trim(),
        description: description || 'Pre-owned item verified and ready for fast delivery.',
        starting_price: price,
        current_bid: price,
        condition: condition || 'Good',
        image_url: finalImage,
        estimated_eco_score: ecoScore,
        status: 'ACTIVE',
        ends_at: endsAt,
        created_at: new Date(),
        bids_count: 0
      };

      memoryDb.products.unshift(newProduct);

      // Update seller eco-score
      const seller = memoryDb.users.find(u => u.id === sellerId);
      if (seller) {
        seller.eco_score = (seller.eco_score || 0) + ecoScore;
      }

      return res.status(201).json({
        status: 'success',
        message: 'Product listed successfully for auction!',
        data: newProduct
      });
    }
  } catch (err) {
    console.error('Error creating product:', err);
    return res.status(500).json({ status: 'error', message: 'Failed to list product' });
  }
};

// Place a Bid on a Product
exports.placeBid = async (req, res) => {
  try {
    const bidderId = req.user.id;
    const productId = req.params.id;
    const { amount } = req.body;

    if (!amount || isNaN(amount)) {
      return res.status(400).json({ status: 'error', message: 'Valid bid amount is required' });
    }

    const bidAmount = parseFloat(amount);

    if (!isMemoryFallback()) {
      const pool = getPool();
      const productResult = await pool.query('SELECT * FROM products WHERE id = $1', [productId]);
      if (productResult.rows.length === 0) {
        return res.status(404).json({ status: 'error', message: 'Product not found' });
      }

      const product = productResult.rows[0];
      if (bidAmount <= parseFloat(product.current_bid)) {
        return res.status(400).json({
          status: 'error',
          message: `Bid amount must be higher than current bid (₹${product.current_bid})`
        });
      }

      // Record bid and update current_bid
      await pool.query('BEGIN');
      const bidInsert = await pool.query(
        'INSERT INTO bids (product_id, bidder_id, amount) VALUES ($1, $2, $3) RETURNING *',
        [productId, bidderId, bidAmount]
      );
      await pool.query('UPDATE products SET current_bid = $1 WHERE id = $2', [bidAmount, productId]);
      await pool.query('COMMIT');

      return res.status(200).json({
        status: 'success',
        message: 'Bid placed successfully!',
        data: {
          currentBid: bidAmount,
          bid: bidInsert.rows[0]
        }
      });
    } else {
      const product = memoryDb.products.find(p => p.id === productId);
      if (!product) {
        return res.status(404).json({ status: 'error', message: 'Product not found' });
      }

      if (bidAmount <= product.current_bid) {
        return res.status(400).json({
          status: 'error',
          message: `Bid amount must be higher than current bid (₹${product.current_bid})`
        });
      }

      product.current_bid = bidAmount;
      product.bids_count = (product.bids_count || 0) + 1;

      const newBid = {
        id: uuidv4(),
        product_id: productId,
        bidder_id: bidderId,
        amount: bidAmount,
        created_at: new Date()
      };
      memoryDb.bids.push(newBid);

      return res.status(200).json({
        status: 'success',
        message: 'Bid placed successfully!',
        data: {
          currentBid: bidAmount,
          bid: newBid
        }
      });
    }
  } catch (err) {
    console.error('Error placing bid:', err);
    return res.status(500).json({ status: 'error', message: 'Failed to place bid' });
  }
};

// AI Suggestion Engine: "One-Click AI Magic Auto-Fill"
exports.aiSuggest = async (req, res) => {
  try {
    const { query, condition, category } = req.body;
    const term = (query || '').toLowerCase().trim();

    // Smart heuristic mock intelligence tailored to Bid.ai resale categories
    let suggestedTitle = 'Apple iPhone 14 (128 GB) - Starlight';
    let suggestedCategoryId = 2; // Mobiles
    let suggestedStartingPrice = 41000;
    let recommendedBuyNow = 48500;
    let estimatedEcoScore = 78;
    let generatedDescription = 'Authentic Apple iPhone 14 in Excellent condition. Battery health 91%, no scratches on display. Supports 5G, FaceID, and MagSafe wireless charging. Fully inspected and reset.';
    let sampleImageUrl = 'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=600&q=80';

    if (term.includes('macbook') || term.includes('laptop') || term.includes('dell') || term.includes('thinkpad')) {
      suggestedTitle = 'Apple MacBook Pro 14" M3 (512GB SSD / 18GB RAM) - Space Black';
      suggestedCategoryId = 3; // Laptops
      suggestedStartingPrice = 115000;
      recommendedBuyNow = 132000;
      estimatedEcoScore = 120;
      generatedDescription = 'Blazing fast Apple M3 Pro chip with Liquid Retina XDR display. Battery health 99% (only 18 charge cycles). Comes with original 70W MagSafe charger and premium hard case.';
      sampleImageUrl = 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600&q=80';
    } else if (term.includes('headphone') || term.includes('sony') || term.includes('airpods') || term.includes('earbuds')) {
      suggestedTitle = 'Sony WH-1000XM5 Premium Noise Canceling Wireless Headphones';
      suggestedCategoryId = 4; // Electronics
      suggestedStartingPrice = 18500;
      recommendedBuyNow = 22000;
      estimatedEcoScore = 48;
      generatedDescription = 'Flagship Active Noise Cancellation with 30-hour battery life. Multi-point connection, pristine sound quality. Includes carrying case, aux cable, and USB-C wire.';
      sampleImageUrl = 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&q=80';
    } else if (term.includes('camera') || term.includes('canon') || term.includes('nikon') || term.includes('sony a7')) {
      suggestedTitle = 'Sony Alpha A7 III Full-Frame Mirrorless Camera (Body Only)';
      suggestedCategoryId = 5; // Cameras
      suggestedStartingPrice = 85000;
      recommendedBuyNow = 98000;
      estimatedEcoScore = 95;
      generatedDescription = 'Professional 24.2MP full-frame BSI sensor with 4K HDR recording. Shutter count 12,000. Sensor pristine, body clean. Includes 2 original NP-FZ100 batteries and dual charger.';
      sampleImageUrl = 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600&q=80';
    } else if (term.includes('chair') || term.includes('table') || term.includes('furniture') || term.includes('desk')) {
      suggestedTitle = 'Steelcase Gesture Ergonomic Executive Mesh Chair';
      suggestedCategoryId = 6; // Furniture
      suggestedStartingPrice = 32000;
      recommendedBuyNow = 39000;
      estimatedEcoScore = 110;
      generatedDescription = 'Award-winning ergonomic work chair designed for all posture styles. 360-degree rotating arms, pneumatic height, tilt lock. Heavy-duty aluminum base.';
      sampleImageUrl = 'https://images.unsplash.com/photo-1580481077195-c3a82145d875?w=600&q=80';
    } else if (term.includes('samsung') || term.includes('galaxy') || term.includes('s23') || term.includes('s24')) {
      suggestedTitle = 'Samsung Galaxy S24 Ultra 5G (256 GB) - Titanium Gray';
      suggestedCategoryId = 2; // Mobiles
      suggestedStartingPrice = 78000;
      recommendedBuyNow = 89000;
      estimatedEcoScore = 80;
      generatedDescription = 'Galaxy AI integrated with Snapdragon 8 Gen 3 processor and built-in S-Pen. Quad telephoto cameras, 120Hz Dynamic AMOLED 2X. 10/10 condition with box and warranty bill.';
      sampleImageUrl = 'https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=600&q=80';
    } else if (term.trim() !== '') {
      // Custom generic item
      const capitalized = term.charAt(0).toUpperCase() + term.slice(1);
      suggestedTitle = `${capitalized} - High Demand Verified Item`;
      suggestedCategoryId = category ? parseInt(category) : 4;
      suggestedStartingPrice = 12500;
      recommendedBuyNow = 15900;
      estimatedEcoScore = 60;
      generatedDescription = `Pre-owned ${capitalized} in ${condition || 'Good'} condition. Cleaned, quality tested, and verified for resale on Bid.ai platform.`;
    }

    return res.status(200).json({
      status: 'success',
      data: {
        suggestedTitle,
        suggestedCategoryId,
        suggestedStartingPrice,
        recommendedBuyNow,
        estimatedEcoScore,
        generatedDescription,
        sampleImageUrl,
        condition: condition || 'Like New',
        confidenceScore: 0.94
      }
    });
  } catch (err) {
    console.error('Error in aiSuggest:', err);
    return res.status(500).json({ status: 'error', message: 'Failed to generate AI suggestion' });
  }
};
